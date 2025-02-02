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

import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.action.ExposedThingAction;
import city.sane.wot.thing.event.ExposedThingEvent;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import city.sane.wot.thing.property.ExposedThingProperty;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static jadex.commons.future.IFuture.DONE;

/**
 * This Agent is responsible for the interaction with the respective Thing. It is started as soon as
 * a thing is to be exposed and terminated when the thing should no longer be exposed.
 */
@Agent
@ProvidedServices({
        @ProvidedService(type = ThingService.class, scope = ServiceScope.GLOBAL)
})
public class ThingAgent implements ThingService {
    private static final Logger log = LoggerFactory.getLogger(ThingAgent.class);
    @Agent
    private IInternalAccess agent;
    @AgentArgument("thing")
    private ExposedThing thing;
    private String thingServiceId;

    public ThingAgent() {
    }

    ThingAgent(IInternalAccess agent, ExposedThing thing) {
        this.agent = agent;
        this.thing = thing;
    }

    @AgentCreated
    public IFuture<Void> created() {
        log.debug("Agent created");

        thingServiceId = getThingServiceId();

        log.debug("Agent has ThingService with id '{}'", thingServiceId);

        //
        // properties
        //

        Map<String, ExposedThingProperty<Object>> properties = thing.getProperties();
        if (!properties.isEmpty()) {
            // make reporting of all properties optional?
            if (true) {
                String href = buildAllPropertiesURI(thingServiceId).toString();
                Form form = new Form.Builder()
                        .setHref(href)
                        .setContentType(ContentManager.DEFAULT)
                        .setOp(Operation.READ_ALL_PROPERTIES, Operation.READ_MULTIPLE_PROPERTIES/*, Operation.writeallproperties, Operation.writemultipleproperties*/)
                        .build();

                thing.addForm(form);
                log.debug("Assign '{}' for reading all properties", href);
            }
        }

        properties.forEach((name, property) -> {
            String href = buildInteractionURI(thingServiceId, "properties", name).toString();
            Form form = new Form.Builder()
                    .setHref(href)
                    .setContentType(ContentManager.DEFAULT)
                    .setOp(Operation.READ_PROPERTY, Operation.WRITE_PROPERTY)
                    .build();
            property.addForm(form);

            log.debug("Assign '{}' to Property '{}'", href, name);
        });

        //
        // actions
        //

        Map<String, ExposedThingAction<Object, Object>> actions = thing.getActions();
        actions.forEach((name, action) -> {
            String href = buildInteractionURI(thingServiceId, "actions", name).toString();
            Form form = new Form.Builder()
                    .setHref(href)
                    .setContentType(ContentManager.DEFAULT)
                    .setOp(Operation.INVOKE_ACTION)
                    .build();
            action.addForm(form);

            log.debug("Assign '{}' to Action '{}'", href, name);
        });

        //
        // events
        //

        Map<String, ExposedThingEvent<Object>> events = thing.getEvents();
        events.forEach((name, event) -> {
            String href = buildInteractionURI(thingServiceId, "events", name).toString();
            Form form = new Form.Builder()
                    .setHref(href)
                    .setContentType(ContentManager.DEFAULT)
                    .setOp(Operation.SUBSCRIBE_EVENT)
                    .build();
            event.addForm(form);

            log.debug("Assign '{}' to Event '{}'", href, name);
        });

        return DONE;
    }

    private URI buildAllPropertiesURI(String serviceInteractionId) {
        return UriComponentsBuilder.newInstance().scheme("jadex").pathSegment(serviceInteractionId, "all", "properties").build().encode().toUri();
    }

    private static URI buildInteractionURI(String serviceId, String type, String name) {
        return UriComponentsBuilder.newInstance().scheme("jadex").pathSegment(serviceId, type, name).build().encode().toUri();
    }

    @AgentKilled
    public void killed() {
        log.debug("Kill Agent with ThingService with id '{}'", thingServiceId);
    }

    @Override
    public IFuture<String> get() {
        return new Future<>(thing.toJson());
    }

    @Override
    public IFuture<JadexContent> readProperties() {
        CompletableFuture<JadexContent> result = thing.readProperties().thenApply(values -> {
            try {
                Content content = ContentManager.valueToContent(values, ContentManager.DEFAULT);
                return new JadexContent(content);
            }
            catch (ContentCodecException e) {
                log.warn("Unable to read properties", e);
                return null;
            }
        });

        return FutureConverters.toJadex(result);
    }

    @Override
    public IFuture<JadexContent> readProperty(String name) {
        ExposedThingProperty<Object> property = thing.getProperty(name);
        CompletableFuture<JadexContent> result = property.read().thenApply(value -> {
            try {
                Content content = ContentManager.valueToContent(value, ContentManager.DEFAULT);
                return new JadexContent(content);
            }
            catch (ContentCodecException e) {
                log.warn("Unable to read property", e);
                return null;
            }
        });

        return FutureConverters.toJadex(result);
    }

    @Override
    public IFuture<JadexContent> writeProperty(String name, JadexContent content) {
        ExposedThingProperty<Object> property = thing.getProperty(name);

        try {
            Object value = ContentManager.contentToValue(content.fromJadex(), property);

            CompletableFuture<JadexContent> result = property.write(value).thenApply(output -> {
                try {
                    Content outputContent = ContentManager.valueToContent(output, ContentManager.DEFAULT);
                    return new JadexContent(outputContent);
                }
                catch (ContentCodecException e) {
                    log.warn("Unable to write property", e);
                    return null;
                }
            });

            return FutureConverters.toJadex(result);
        }
        catch (ContentCodecException e) {
            return new Future<>(e);
        }
    }

    @Override
    public String getThingServiceId() {
        return ((IService) agent.getProvidedService(ThingService.class)).getServiceId().toString();
    }
}
