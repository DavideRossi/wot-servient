package city.sane.wot.binding.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import city.sane.Pair;
import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.binding.ProtocolClientNotImplementedException;
import city.sane.wot.binding.akka.Messages.*;
import city.sane.wot.binding.akka.actor.ObserveActor;
import city.sane.wot.binding.akka.actor.ThingsActor.Discover;
import city.sane.wot.content.Content;
import city.sane.wot.thing.Thing;
import city.sane.wot.thing.filter.ThingFilter;
import city.sane.wot.thing.form.Form;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static city.sane.wot.binding.akka.actor.ThingsActor.Things;
import static java.util.concurrent.CompletableFuture.failedFuture;

/**
 * Allows consuming Things via Akka Actors.<br> The Actor System created by {@link
 * AkkaProtocolClientFactory} is used for this purpose and thus enables interaction with exposed
 * Things on other actuator systems.
 */
public class AkkaProtocolClient implements ProtocolClient {
    private static final Logger log = LoggerFactory.getLogger(AkkaProtocolClient.class);
    private final ActorSystem system;
    private final ActorRef discoveryActor;
    private final AkkaProtocolPattern pattern;

    public AkkaProtocolClient(ActorSystem system, ActorRef discoveryActor) {
        this(system, discoveryActor, new AkkaProtocolPattern());
    }

    AkkaProtocolClient(ActorSystem system, ActorRef discoveryActor, AkkaProtocolPattern pattern) {
        this.system = system;
        this.discoveryActor = discoveryActor;
        this.pattern = pattern;
    }

    @SuppressWarnings("squid:S1192")
    @Override
    public CompletableFuture<Content> readResource(Form form) {
        Read message = new Read();
        String href = form.getHref();
        if (href == null) {
            return failedFuture(new ProtocolClientException("no href given"));
        }
        log.debug("AkkaClient sending '{}' to {}", message, href);

        ActorSelection selection = system.actorSelection(href);
        Duration timeout = Duration.ofSeconds(10);
        return pattern.ask(selection, message, timeout)
                .thenApply(m -> ((RespondRead) m).content)
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<Content> writeResource(Form form, Content content) {
        Write message = new Write(content);
        String href = form.getHref();
        if (href == null) {
            return failedFuture(new ProtocolClientException("no href given"));
        }
        log.debug("AkkaClient sending '{}' to {}", message, href);

        ActorSelection selection = system.actorSelection(href);
        Duration timeout = Duration.ofSeconds(10);
        return pattern.ask(selection, message, timeout)
                .thenApply(m -> ((Written) m).content)
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<Content> invokeResource(Form form, Content content) {
        Invoke message = new Invoke(content);
        String href = form.getHref();
        if (href == null) {
            return failedFuture(new ProtocolClientException("no href given"));
        }
        log.debug("AkkaClient sending '{}' to {}", message, href);

        ActorSelection selection = system.actorSelection(href);
        Duration timeout = Duration.ofSeconds(10);
        return pattern.ask(selection, message, timeout)
                .thenApply(m -> ((Invoked) m).content)
                .toCompletableFuture();
    }

    @Override
    public Observable<Content> observeResource(Form form) throws ProtocolClientException {
        String href = form.getHref();
        if (href == null) {
            throw new ProtocolClientException("no href given");
        }
        ActorSelection selection = system.actorSelection(href);

        return Observable.using(
                () -> {
                    log.debug("Create temporary actor to observe resource: {}", href);
                    PublishSubject<Content> subject = PublishSubject.create();
                    ActorRef actorRef = system.actorOf(ObserveActor.props(subject, selection));
                    return new Pair<>(subject, actorRef);
                },
                Pair::first,
                pair -> {
                    log.debug("No more observers. Stop temporary actor form resource observation: {}", href);
                    system.stop(pair.second());
                }
        );
    }

    @Override
    public CompletableFuture<Collection<Thing>> discover(ThingFilter filter) {
        if (system.settings().config().getStringList("wot.servient.akka.extensions").contains("akka.cluster.pubsub.DistributedPubSub")) {
            Discover message = new Discover(filter);
            log.debug("AkkaClient sending '{}' to {}", message, discoveryActor);

            Duration timeout = Duration.ofSeconds(10);
            return pattern.ask(discoveryActor, message, timeout)
                    .thenApply(m -> ((Things) m).entities.values())
                    .toCompletableFuture();
        }
        else {
            log.warn("DistributedPubSub extension missing. ANY Discovery is not be supported.");
            return failedFuture(new ProtocolClientNotImplementedException(getClass(), "discover"));
        }
    }
}
