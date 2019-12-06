package city.sane.wot.binding.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import city.sane.wot.thing.property.ExposedThingProperty;

import java.io.Serializable;
import java.util.Arrays;

import static city.sane.wot.binding.akka.CrudMessages.Created;
import static city.sane.wot.binding.akka.Messages.*;

/**
 * This actor is responsible for the interaction with a {@link ExposedThingProperty}.
 */
public class PropertyActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String name;
    private final ExposedThingProperty property;

    public PropertyActor(String name, ExposedThingProperty property) {
        this.name = name;
        this.property = property;
    }

    @Override
    public void preStart() {
        log.info("Started");

        String href = getSelf().path().toStringWithAddress(getContext().getSystem().provider().getDefaultAddress());
        Form.Builder builder = new Form.Builder()
                .setHref(href)
                .setContentType(ContentManager.DEFAULT);
        if (property.isReadOnly()) {
            builder.setOp(Operation.READ_PROPERTY);
        }
        else if (property.isWriteOnly()) {
            builder.setOp(Operation.WRITE_PROPERTY);
        }
        else {
            builder.setOp(Arrays.asList(Operation.READ_PROPERTY, Operation.WRITE_PROPERTY));
        }

        property.addForm(builder.build());
        log.info("Assign '{}' to Property '{}'", href, name);

        getContext().getParent().tell(new Created<>(getSelf()), getSelf());

        // TODO: add support for property observation
    }

    @Override
    public void postStop() {
        log.info("Stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Read.class, this::read)
                .match(Write.class, this::write)
                .match(Subscribe.class, this::subscribe)
                .build();
    }

    private void read(Read m) {
        ActorRef sender = getSender();

        property.read().whenComplete((value, e) -> {
            if (e != null) {
                log.warning("Unable to read property: {}", e.getMessage());
            }
            else {
                try {
                    Content content = ContentManager.valueToContent(value);
                    sender.tell(new RespondRead(content), getSelf());
                }
                catch (ContentCodecException ex) {
                    log.warning("Unable to read property: {}", ex.getMessage());
                }
            }
        });
    }

    private void write(Write m) {
        try {
            ActorRef sender = getSender();
            Content inputContent = m.content;
            Object input = ContentManager.contentToValue(inputContent, property);

            property.write(input).whenComplete((output, e) -> {
                if (e != null) {
                    log.warning("Unable to write property: {}", e.getMessage());
                }
                else {
                    // TODO: return output if available
                    sender.tell(new Written(new Content(ContentManager.DEFAULT, new byte[0])), getSelf());
                }
            });

        }
        catch (ContentCodecException e) {
            log.warning("Unable to write property: {}", e.getMessage());
        }
    }

    private void subscribe(Subscribe m) {
        // FIXME: Implement
    }

    public static Props props(String name, ExposedThingProperty property) {
        return Props.create(PropertyActor.class, () -> new PropertyActor(name, property));
    }

    public static class Subscribe implements Serializable {
        // FIXME: Implement
    }
}