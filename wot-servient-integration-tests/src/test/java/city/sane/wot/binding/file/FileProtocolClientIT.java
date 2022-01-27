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
package city.sane.wot.binding.file;

import city.sane.wot.content.Content;
import city.sane.wot.thing.form.Form;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileProtocolClientIT {
    private Path thing;

    @BeforeEach
    public void setup(@TempDir Path thingDirectory) throws IOException {
        thing = Paths.get(thingDirectory.toString(), "ThingA.json");
        Files.writeString(thing, "{\"id\":\"counter\",\"name\":\"Counter\"}", StandardOpenOption.CREATE);
    }

    @Test
    public void readResourceAbsoluteUrl() throws ExecutionException, InterruptedException {
        FileProtocolClient client = new FileProtocolClient();
        String href = thing.toUri().toString();
        Form form = new Form.Builder()
                .setHref(href)
                .build();

        Content content = client.readResource(form).get();

        assertEquals("application/json", content.getType());
        assertEquals("{\"id\":\"counter\",\"name\":\"Counter\"}", new String(content.getBody()));
    }

    @Test
    public void writeResourceAbsoluteUrl() throws ExecutionException, InterruptedException, IOException {
        FileProtocolClient client = new FileProtocolClient();
        String href = thing.toUri().toString();
        Form form = new Form.Builder()
                .setHref(href)
                .build();

        Content content = new Content("application/json", "{\"id\":\"counter\",\"name\":\"Zähler\"}".getBytes());
        client.writeResource(form, content).get();

        assertEquals("{\"id\":\"counter\",\"name\":\"Zähler\"}", Files.readString(thing));
    }

    @Test
    public void subscribeResourceFileChanged() throws ExecutionException, InterruptedException, IOException, TimeoutException {
        FileProtocolClient client = new FileProtocolClient();
        String href = thing.toUri().toString();
        Form form = new Form.Builder()
                .setHref(href)
                .build();

        Future<Content> future = client.observeResource(form).firstElement().toFuture();

        // wait until client has established subscription
        await().atMost(Duration.ofSeconds(10))
                .untilAtomic(
                        fieldIn(client).ofType(AtomicInteger.class).andWithName("subscriptionsCount").call(),
                        equalTo(1)
                );

        Files.writeString(thing, "{\"id\":\"counter\",\"name\":\"Zähler\"}", StandardOpenOption.CREATE);

        assertEquals("{\"id\":\"counter\",\"name\":\"Zähler\"}", new String(future.get(10, TimeUnit.SECONDS).getBody()));
    }

    @Test
    public void subscribeResourceFileCreated() throws ExecutionException, InterruptedException, IOException, TimeoutException {
        FileProtocolClient client = new FileProtocolClient();
        String href = thing.toUri().toString();
        Form form = new Form.Builder()
                .setHref(href)
                .build();

        thing.toFile().delete();

        Future<Content> future = client.observeResource(form).firstElement().toFuture();

        // wait until client has established subscription
        await().atMost(Duration.ofSeconds(10))
                .untilAtomic(
                        fieldIn(client).ofType(AtomicInteger.class).andWithName("subscriptionsCount").call(),
                        equalTo(1)
                );

        Files.writeString(thing, "{\"id\":\"counter\",\"name\":\"Zähler\"}", StandardOpenOption.CREATE);

        assertEquals("{\"id\":\"counter\",\"name\":\"Zähler\"}", new String(future.get(20, TimeUnit.SECONDS).getBody()));
    }

    @Test
    public void subscribeResourceFileDeleted() throws ExecutionException, InterruptedException, TimeoutException {
        FileProtocolClient client = new FileProtocolClient();
        String href = thing.toUri().toString();
        Form form = new Form.Builder()
                .setHref(href)
                .build();

        Future<Content> future = client.observeResource(form).firstElement().toFuture();

        // wait until client has established subscription
        await().atMost(Duration.ofSeconds(10))
                .untilAtomic(
                        fieldIn(client).ofType(AtomicInteger.class).andWithName("subscriptionsCount").call(),
                        equalTo(1)
                );

        thing.toFile().delete();

        assertEquals(Content.EMPTY_CONTENT, future.get(20, TimeUnit.SECONDS));
    }
}