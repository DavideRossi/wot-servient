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
package city.sane.wot.binding.coap;

import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoapProtocolClientIT {
    private CoapProtocolClientFactory clientFactory;
    private CoapProtocolClient client;
    private CoapServer server;
    private int port;

    @BeforeEach
    public void setUp() {
        clientFactory = new CoapProtocolClientFactory();
        clientFactory.init().join();

        client = clientFactory.getClient();

        server = new CoapServer(0);
        server.add(new MyReadResource());
        server.add(new MyWriteResource());
        server.add(new MyInvokeResource());
        server.add(new MySubscribeResource());
        server.start();
        port = server.getEndpoints().get(0).getAddress().getPort();
    }

    @AfterEach
    public void tearDown() {
        clientFactory.destroy().join();
        server.stop();
    }

    @Test
    public void readResource() throws ContentCodecException, ExecutionException, InterruptedException {
        String href = "coap://localhost:" + port + "/read";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(ContentManager.valueToContent(1337), client.readResource(form).get());
    }

    @Test
    public void writeResource() throws ContentCodecException, ExecutionException, InterruptedException {
        String href = "coap://localhost:" + port + "/write";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(ContentManager.valueToContent(42), client.writeResource(form, ContentManager.valueToContent(1337)).get());
    }

    @Test
    public void invokeResource() throws ContentCodecException, ExecutionException, InterruptedException {
        String href = "coap://localhost:" + port + "/invoke";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(ContentManager.valueToContent(42), client.invokeResource(form, ContentManager.valueToContent(1337)).get());
    }

    @Test
    @Timeout(value = 5, unit = SECONDS)
    public void subscribeResource() throws ContentCodecException {
        String href = "coap://localhost:" + port + "/subscribe";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(
                ContentManager.valueToContent(42),
                client.observeResource(form).firstElement().blockingGet()
        );
    }

    class MyReadResource extends CoapResource {
        MyReadResource() {
            super("read");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            int contentFormat = MediaTypeRegistry.parse("application/json");
            exchange.respond(CoAP.ResponseCode.CONTENT, "1337", contentFormat);
        }
    }

    class MyWriteResource extends CoapResource {
        MyWriteResource() {
            super("write");
        }

        @Override
        public void handlePUT(CoapExchange exchange) {
            int contentFormat = MediaTypeRegistry.parse("application/json");
            exchange.respond(CoAP.ResponseCode.CONTENT, "42", contentFormat);
        }
    }

    class MyInvokeResource extends CoapResource {
        MyInvokeResource() {
            super("invoke");
        }

        @Override
        public void handlePOST(CoapExchange exchange) {
            int contentFormat = MediaTypeRegistry.parse("application/json");
            exchange.respond(CoAP.ResponseCode.CONTENT, "42", contentFormat);
        }
    }

    class MySubscribeResource extends CoapResource {
        MySubscribeResource() {
            super("subscribe");

            setObservable(true); // enable observing
            setObserveType(CoAP.Type.CON); // configure the notification type to CONs
            getAttributes().setObservable(); // mark observable in the Link-Format
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            int contentFormat = MediaTypeRegistry.parse("application/json");
            exchange.respond(CoAP.ResponseCode.CONTENT, "42", contentFormat);
        }
    }
}