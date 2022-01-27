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
package city.sane.wot.examples;

import city.sane.wot.DefaultWot;
import city.sane.wot.Wot;
import city.sane.wot.WotException;
import city.sane.wot.thing.ConsumedThing;
import city.sane.wot.thing.ConsumedThingException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Fetch thing description exposes by {@link ExampleEvent} and then subscribe to the event.
 */
@SuppressWarnings({ "java:S106", "java:S112" })
class ExampleEventClient {
    public static void main(String[] args) throws URISyntaxException, IOException, WotException {
        // create wot
        Wot wot = DefaultWot.clientOnly();

        URI url = new URI("coap://localhost:5683/EventSource");
        wot.fetch(url).whenComplete((thing, e) -> {
            if (e != null) {
                throw new RuntimeException(e);
            }

            System.out.println("=== TD ===");
            String json = thing.toJson(true);
            System.out.println(json);
            System.out.println("==========");

            ConsumedThing consumedThing = wot.consume(thing);

            try {
                consumedThing.getEvent("onchange").observer().subscribe(
                        next -> System.out.println("ExampleDynamicClient: next = " + next),
                        ex -> System.out.println("ExampleDynamicClient: error = " + ex.toString()),
                        () -> System.out.println("ExampleDynamicClient: completed!")
                );
                System.out.println("ExampleDynamicClient: Subscribed");
            }
            catch (ConsumedThingException ex) {
                throw new RuntimeException(e);
            }
        }).join();

        System.out.println("Press ENTER to exit the client");
        System.in.read();
    }
}
