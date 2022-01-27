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
import city.sane.wot.thing.Context;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.Thing;
import city.sane.wot.thing.property.ThingProperty;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This example exposes a Thing that can be discovered by other Actor Systems.
 */
@SuppressWarnings("squid:S106")
class AkkaDiscovery {
    public static void main(String[] args) throws WotException {
        // create wot
        Config config = ConfigFactory
                .parseString("wot.servient.servers = [\"city.sane.wot.binding.akka.AkkaProtocolServer\"]")
                .withFallback(ConfigFactory.load());
        Wot wot = DefaultWot.serverOnly(config);

        // create thing
        Thing thing = new Thing.Builder()
                .setId("HelloClient")
                .setTitle("HelloClient")
                .setObjectType("Thing")
                .setObjectContext(new Context("https://www.w3.org/2019/wot/td/v1")
                        .addContext("saref", "https://w3id.org/saref#")
                )
                .build();
        ExposedThing exposedThing = wot.produce(thing);

        exposedThing.addProperty("Temperature", new ThingProperty.Builder().setObjectType("saref:Temperature").build(), 15);
        exposedThing.addProperty("Luftdruck", new ThingProperty.Builder().setObjectType("saref:Pressure").build(), 32);

        System.out.println(exposedThing.toJson(true));

        exposedThing.expose();
    }
}
