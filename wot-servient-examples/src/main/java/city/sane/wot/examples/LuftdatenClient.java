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
import city.sane.wot.thing.Thing;
import city.sane.wot.thing.filter.DiscoveryMethod;
import city.sane.wot.thing.filter.ThingFilter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Fetch and consume first thing description exposes by {@link Luftdaten} and then read some
 * properties.
 */
@SuppressWarnings({ "java:S106" })
class LuftdatenClient {
    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException, WotException {
        // create wot
        Wot wot = DefaultWot.clientOnly();

        ThingFilter filter = new ThingFilter()
                .setMethod(DiscoveryMethod.DIRECTORY)
                .setUrl(new URI("coap://localhost"));
        Thing thing = wot.discover(filter).firstElement().blockingGet();

        System.out.println("=== TD ===");
        String json = thing.toJson(true);
        System.out.println(json);
        System.out.println("==========");

        ConsumedThing consumedThing = wot.consume(thing);

        CompletableFuture<Object> future1 = consumedThing.getProperty("latitude").read();
        Object latitude = future1.get();
        System.out.println("latitude = " + latitude);

        CompletableFuture<Object> future2 = consumedThing.getProperty("P1").read();
        Object p1 = future2.get();
        System.out.println("P1 = " + p1);
    }
}
