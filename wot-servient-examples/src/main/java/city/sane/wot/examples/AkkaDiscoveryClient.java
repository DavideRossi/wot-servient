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
import city.sane.wot.thing.filter.SparqlThingQuery;
import city.sane.wot.thing.filter.ThingFilter;
import city.sane.wot.thing.filter.ThingQuery;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This examples uses Akka's cluster functionality to discovery (remote) things exposed by {@link
 * AkkaDiscovery}.
 */
@SuppressWarnings({ "java:S106" })
class AkkaDiscoveryClient {
    public static void main(String[] args) throws WotException {
        // create wot
        Config config = ConfigFactory
                .parseString("wot.servient.client-factories = [\"city.sane.wot.binding.akka.AkkaProtocolClientFactory\"]\nwot.servient.akka.remote.artery.canonical.port = 0")
                .withFallback(ConfigFactory.load());
        Wot wot = DefaultWot.clientOnly(config);

        // Search for things providing a Temperature
        ThingQuery query = new SparqlThingQuery("?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/saref#Temperature> .");
        Collection<Thing> things = wot.discover(new ThingFilter().setQuery(query)).toList().blockingGet();

        System.out.println("Found " + things.size() + " thing(s)");

        if (!things.isEmpty()) {
            // print found things
            things.forEach(t -> {
                System.out.println("=== TD ===");
                System.out.println(t.toJson(true));
                ConsumedThing ct = wot.consume(t);

                try {
                    Map<String, Object> properties = ct.readProperties().get();
                    properties.forEach((key, value) -> System.out.println(key + ": " + value));
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("==========");
            });
        }
    }
}
