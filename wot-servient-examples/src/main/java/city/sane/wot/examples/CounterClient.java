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

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Fetch thing description exposes by {@link Counter} and then interact with it.
 */
@SuppressWarnings({ "java:S106", "java:S1192" })
class CounterClient {
    public static void main(String[] args) throws URISyntaxException, WotException {
        // create wot
        Wot wot = DefaultWot.clientOnly();

        wot.fetch("coap://localhost:5683/counter").whenComplete((thing, e) -> {
            try {
                if (e != null) {
                    throw new RuntimeException(e);
                }

                System.out.println("=== TD ===");
                String json = thing.toJson(true);
                System.out.println(json);
                System.out.println("==========");

                ConsumedThing consumedThing = wot.consume(thing);

                // read property #1
                Object read1 = consumedThing.getProperty("count").read().get();
                System.out.println("CounterClient: count value is " + read1);

                // increment property #1
                consumedThing.getAction("increment").invoke().get();
                Object inc1 = consumedThing.getProperty("count").read().get();
                System.out.println("CounterClient: count value after increment #1 is " + inc1);

                // increment property #2
                consumedThing.getAction("increment").invoke().get();
                Object inc2 = consumedThing.getProperty("count").read().get();
                System.out.println("CounterClient: count value after increment #2 is " + inc2);

                // decrement property
                consumedThing.getAction("decrement").invoke().get();
                Object dec1 = consumedThing.getProperty("count").read().get();
                System.out.println("CounterClient: count value after decrement is " + dec1);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }).join();
    }
}
