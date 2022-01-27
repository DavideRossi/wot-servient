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
package city.sane.wot.binding.http;

import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpProtocolClientIT {
    private HttpProtocolClientFactory clientFactory;
    private ProtocolClient client;
    private Service service;

    @BeforeEach
    public void setUp() {
        clientFactory = new HttpProtocolClientFactory();
        clientFactory.init().join();

        client = clientFactory.getClient();
        service = Service.ignite().ipAddress("127.0.0.1").port(8080);
        service.init();
        service.awaitInitialization();
        service.get("read", new MyReadRoute());
        service.put("write", new MyWriteRoute());
        service.post("invoke", new MyInvokeRoute());
        service.get("subscribe", new MySubscribeRoute());
        service.get("status/300", new MyStatusRoute());
        service.get("status/400", new MyStatusRoute());
        service.get("status/500", new MyStatusRoute());
    }

    @AfterEach
    public void tearDown() {
        clientFactory.destroy().join();

        service.stop();
        service.awaitStop();
    }

    @Test
    public void readResource() throws ContentCodecException, ExecutionException, InterruptedException {
        String href = "http://localhost:8080/read";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(ContentManager.valueToContent(1337), client.readResource(form).get());
    }

    @Test
    public void readResourceRedirection() {
        String href = "http://localhost:8080/status/300";
        Form form = new Form.Builder().setHref(href).build();

        assertThrows(ProtocolClientException.class, () -> {
            try {
                client.readResource(form).get();
            }
            catch (ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void readResourceClientError() {
        String href = "http://localhost:8080/status/400";
        Form form = new Form.Builder().setHref(href).build();

        assertThrows(ProtocolClientException.class, () -> {
            try {
                client.readResource(form).get();
            }
            catch (ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void readResourceServerError() {
        String href = "http://localhost:8080/status/500";
        Form form = new Form.Builder().setHref(href).build();

        assertThrows(ProtocolClientException.class, () -> {
            try {
                client.readResource(form).get();
            }
            catch (ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void writeResource() throws ContentCodecException, ExecutionException, InterruptedException {
        String href = "http://localhost:8080/write";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(ContentManager.valueToContent(42), client.writeResource(form, ContentManager.valueToContent(1337)).get());
    }

    @Test
    public void invokeResource() throws ContentCodecException, ExecutionException, InterruptedException {
        String href = "http://localhost:8080/invoke";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(ContentManager.valueToContent(42), client.invokeResource(form, ContentManager.valueToContent(1337)).get());
    }

    @Test
    @Timeout(value = 5, unit = SECONDS)
    public void subscribeResource() throws ProtocolClientException, ExecutionException, InterruptedException, ContentCodecException {
        String href = "http://localhost:8080/subscribe";
        Form form = new Form.Builder().setHref(href).build();

        assertEquals(
                ContentManager.valueToContent(1337),
                client.observeResource(form).firstElement().blockingGet()
        );
    }

    private class MyReadRoute implements Route {
        @Override
        public Object handle(Request request, Response response) {
            response.type("application/json");
            return 1337;
        }
    }

    private class MyWriteRoute implements Route {
        @Override
        public Object handle(Request request, Response response) {
            response.type("application/json");
            return 42;
        }
    }

    private class MyInvokeRoute implements Route {
        @Override
        public Object handle(Request request, Response response) {
            response.type("application/json");
            return 42;
        }
    }

    private class MySubscribeRoute implements Route {
        @Override
        public Object handle(Request request, Response response) {
            response.type("application/json");
            return 1337;
        }
    }

    private class MyStatusRoute implements Route {
        @Override
        public Object handle(Request request, Response response) throws Exception {
            int statusCode = Integer.parseInt(request.pathInfo().split("/", 3)[2]);
            response.status(statusCode);
            return "";
        }
    }
}