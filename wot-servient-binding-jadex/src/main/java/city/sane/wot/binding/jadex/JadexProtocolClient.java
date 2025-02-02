/*
 * Copyright (c) 2019-2022 Heiko Bornholdt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package city.sane.wot.binding.jadex;

import city.sane.Pair;
import city.sane.Triple;
import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.content.Content;
import city.sane.wot.thing.Thing;
import city.sane.wot.thing.filter.ThingFilter;
import city.sane.wot.thing.form.Form;
import io.reactivex.rxjava3.core.Observable;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.TimeoutException;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static jadex.commons.future.IFuture.DONE;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

/**
 * Allows consuming Things via Jadex Micro Agents. The Jadex Platform created by {@link
 * JadexProtocolClientFactory} is used for this purpose and thus enables interaction with exposed
 * Things on other platforms.
 */
public class JadexProtocolClient implements ProtocolClient {
    private static final Logger log = LoggerFactory.getLogger(JadexProtocolClient.class);
    private final IExternalAccess platform;

    public JadexProtocolClient(IExternalAccess platform) {
        this.platform = platform;
    }

    @Override
    public CompletableFuture<Content> readResource(Form form) {
        try {
            Triple<String, String, String> triple = parseAsReadResourceHref(form.getHref());
            String serviceId = triple.first();
            String type = triple.second();
            String name = triple.third();

            CompletableFuture<Content> result = new CompletableFuture<>();
            platform.scheduleStep(ia -> {
                getThingService(ia, serviceId).whenComplete((service, e) -> {
                    if (e == null) {
                        log.trace("Found service {}", service);

                        JadexContent content;
                        if (type.equals("all")) {
                            log.trace("Read properties");
                            content = service.readProperties().get();
                            log.trace("Done reading properties");
                        }
                        else {
                            log.trace("Read property");
                            content = service.readProperty(name).get();
                            log.trace("Done reading property");
                        }
                        result.complete(content.fromJadex());
                    }
                    else {
                        result.completeExceptionally(e);
                    }
                });
                return DONE;
            });
            return result;
        }
        catch (ProtocolClientException e) {
            throw new CompletionException(e);
        }
    }

    @Override
    public CompletableFuture<Content> writeResource(Form form, Content content) {
        try {
            log.debug("Write resource '{}'", form.getHref());
            Pair<String, String> pair = parseAsWriteResourceHref(form.getHref());
            String serviceId = pair.first();
            String name = pair.second();

            CompletableFuture<Content> result = new CompletableFuture<>();
            platform.scheduleStep(ia -> {
                getThingService(ia, serviceId).whenComplete((service, e) -> {
                    if (e == null) {
                        log.trace("Found service {}", service);

                        JadexContent input = new JadexContent(content);
                        JadexContent output = service.writeProperty(name, input).get();
                        result.complete(output.fromJadex());
                    }
                    else {
                        result.completeExceptionally(e);
                    }
                });
                return DONE;
            });
            return result;
        }
        catch (ProtocolClientException e) {
            throw new CompletionException(e);
        }
    }

    @Override
    public Observable<Thing> discover(ThingFilter filter) {
        return Observable
                .<ThingService>create(source -> platform.scheduleStep(ia -> {
                    ServiceQuery<ThingService> query = new ServiceQuery<>(ThingService.class, ServiceScope.GLOBAL);
                    ITerminableIntermediateFuture<ThingService> search = ia.searchServices(query);
                    search.addResultListener(new IntermediateDefaultResultListener<>() {
                        public void intermediateResultAvailable(ThingService result) {
                            source.onNext(result);
                        }

                        @Override
                        public void finished() {
                            source.onComplete();
                        }

                        @Override
                        public void exceptionOccurred(Exception exception) {
                            source.onError(exception);
                        }
                    });
                    return DONE;
                }))
                .flatMap(thingService -> Observable.<Thing>create(source -> thingService.get().addResultListener(new IResultListener<>() {
                    @Override
                    public void resultAvailable(String json) {
                        source.onNext(Thing.fromJson(json));
                        source.onComplete();
                    }

                    @Override
                    public void exceptionOccurred(Exception exception) {
                        source.onError(exception);
                    }
                })))
                .filter(thing -> {
                    if (filter.getQuery() != null) {
                        // TODO: move filter to server-side
                        return !filter.getQuery().filter(List.of(thing)).isEmpty();
                    }
                    else {
                        return true;
                    }
                });
    }

    private Pair<String, String> parseAsWriteResourceHref(String href) throws ProtocolClientException {
        try {
            URI uri = new URI(href);

            String path = uri.getPath();
            if (path.startsWith("/ThingService_")) {
                String[] pathFragments = path.split("/", 4);
                if (pathFragments.length == 4) {
                    String serviceId = pathFragments[1];
                    String name = pathFragments[3];

                    return new Pair<>(serviceId, name);
                }
                else {
                    throw new ProtocolClientException("Bad href path: " + path);
                }
            }
            else {
                throw new ProtocolClientException("Href points to unexpected agent: " + uri);
            }
        }
        catch (URISyntaxException e) {
            throw new ProtocolClientException(e);
        }
    }

    private Triple<String, String, String> parseAsReadResourceHref(String href) throws ProtocolClientException {
        try {
            URI uri = new URI(href);

            String path = uri.getPath();
            if (path.startsWith("/ThingService_")) {
                String[] pathFragments = path.split("/", 4);
                if (pathFragments.length == 4) {
                    String serviceId = pathFragments[1];
                    String type = pathFragments[2];
                    String name = pathFragments[3];

                    return new Triple<>(serviceId, type, name);
                }
                else {
                    throw new ProtocolClientException("Bad href path: " + path);
                }
            }
            else {
                throw new ProtocolClientException("Href points to unexpected agent: " + uri);
            }
        }
        catch (URISyntaxException e) {
            throw new ProtocolClientException(e);
        }
    }

    private CompletableFuture<ThingService> getThingService(IInternalAccess agent,
                                                            String serviceId) {
        log.debug("Search ThingService with id '{}'", serviceId);
        ServiceQuery<ThingService> query = new ServiceQuery(ThingService.class)
                .setScope(ServiceScope.GLOBAL)
                .setId(serviceId);
        try {
            return completedFuture(SFuture.getFirstResultAndTerminate(agent.addQuery(query)));
        }
        catch (TimeoutException e) {
            log.warn("Search ThingService with id '{}' failed: {}", serviceId, e.getMessage());
            return failedFuture(e);
        }
    }
}
