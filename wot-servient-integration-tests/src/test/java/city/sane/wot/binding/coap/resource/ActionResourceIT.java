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
package city.sane.wot.binding.coap.resource;

import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.action.ThingAction;
import city.sane.wot.thing.event.ThingEvent;
import city.sane.wot.thing.property.ThingProperty;
import city.sane.wot.thing.schema.IntegerSchema;
import city.sane.wot.thing.schema.NumberSchema;
import city.sane.wot.thing.schema.ObjectSchema;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionResourceIT {
    private CoapServer server;
    private int port;

    @BeforeEach
    public void setup() {
        ExposedThing thing = getCounterThing();

        server = new CoapServer(0);
        server.add(new ActionResource("increment", thing.getAction("increment")));
        server.start();
        port = server.getEndpoints().get(0).getAddress().getPort();
    }

    private ExposedThing getCounterThing() {
        ExposedThing thing = new ExposedThing(null)
                .setId("counter")
                .setTitle("counter")
                .setDescription("counter example Thing");

        ThingProperty counterProperty = new ThingProperty.Builder()
                .setType("integer")
                .setDescription("current counter value")
                .setObservable(true)
                .setUriVariables(Map.of(
                        "step", Map.of(
                                "type", "integer",
                                "minimum", 1,
                                "maximum", 250
                        )
                ))
                .build();

        ThingProperty lastChangeProperty = new ThingProperty.Builder()
                .setType("string")
                .setDescription("last change of counter value")
                .setObservable(true)
                .setReadOnly(true)
                .build();

        thing.addProperty("count", counterProperty, 42);
        thing.addProperty("lastChange", lastChangeProperty, new Date().toString());

        thing.addAction("increment", new ThingAction.Builder()
                .setInput(new ObjectSchema())
                .setOutput(new NumberSchema())
                .build(), (input, options) -> {
            return thing.getProperty("count").read().thenApply(value -> {
                int step;
                if ((input instanceof Map) && ((Map) input).containsKey("step")) {
                    step = (int) ((Map) input).get("step");
                }
                else {
                    step = 1;
                }
                int newValue = ((Integer) value) + step;
                thing.getProperty("count").write(newValue);
                thing.getProperty("lastChange").write(new Date().toString());
                thing.getEvent("change").emit();
                return newValue;
            });
        });

        thing.addAction("decrement", new ThingAction<Object, Object>(), (input, options) -> {
            return thing.getProperty("count").read().thenApply(value -> {
                int newValue = ((Integer) value) - 1;
                thing.getProperty("count").write(newValue);
                thing.getProperty("lastChange").write(new Date().toString());
                thing.getEvent("change").emit();
                return newValue;
            });
        });

        thing.addAction("reset", new ThingAction<Object, Object>(), (input, options) -> {
            return thing.getProperty("count").write(0).thenApply(value -> {
                thing.getProperty("lastChange").write(new Date().toString());
                thing.getEvent("change").emit();
                return 0;
            });
        });

        thing.addEvent("change", new ThingEvent<Object>());

        return thing;
    }

    @AfterEach
    public void teardown() {
        server.stop();
    }

    @Test
    public void invokeAction() throws ContentCodecException {
        CoapClient client = new CoapClient("coap://localhost:" + port + "/increment");
        CoapResponse response = client.post("", MediaTypeRegistry.APPLICATION_JSON);

        assertEquals(CoAP.ResponseCode.CONTENT, response.getCode());

        int responseContentType = response.getOptions().getContentFormat();
        assertEquals(MediaTypeRegistry.APPLICATION_JSON, responseContentType);

        Content content = new Content(MediaTypeRegistry.toString(responseContentType), response.getPayload());
        Object responseValue = ContentManager.contentToValue(content, new IntegerSchema());
        assertThat(responseValue, instanceOf(Integer.class));

        assertEquals(43, responseValue);
    }

    @Test
    public void invokeActionWithCustomContentType() throws ContentCodecException {
        CoapClient client = new CoapClient("coap://localhost:" + port + "/increment");
        Request request = new Request(CoAP.Code.POST);
        request.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_CBOR);
        CoapResponse response = client.advanced(request);

        assertEquals(CoAP.ResponseCode.CONTENT, response.getCode());

        int responseContentType = response.getOptions().getContentFormat();
        assertEquals(MediaTypeRegistry.APPLICATION_CBOR, responseContentType);

        Content content = new Content(MediaTypeRegistry.toString(responseContentType), response.getPayload());
        int responseValue = ContentManager.contentToValue(content, new IntegerSchema());
        assertThat(responseValue, instanceOf(Integer.class));

        assertEquals(43, responseValue);
    }

    @Test
    public void invokeActionWithParameters() throws ContentCodecException {
        CoapClient client = new CoapClient("coap://localhost:" + port + "/increment");
        Content inputContent = ContentManager.valueToContent(Map.of("step", 3), "application/json");
        CoapResponse response = client.post(inputContent.getBody(), MediaTypeRegistry.APPLICATION_JSON);

        assertEquals(CoAP.ResponseCode.CONTENT, response.getCode());

        int responseContentType = response.getOptions().getContentFormat();
        assertEquals(MediaTypeRegistry.APPLICATION_JSON, responseContentType);

        Content outputContent = new Content(MediaTypeRegistry.toString(responseContentType), response.getPayload());
        int responseValue = ContentManager.contentToValue(outputContent, new IntegerSchema());
        assertThat(responseValue, instanceOf(Integer.class));

        assertEquals(45, responseValue);
    }
}