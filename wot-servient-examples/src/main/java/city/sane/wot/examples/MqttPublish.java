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
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.Thing;
import city.sane.wot.thing.event.ThingEvent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produces a thing that sends a ascending counter to an MQTT topic.
 */
@SuppressWarnings({ "java:S106" })
class MqttPublish {
    public static void main(String[] args) throws WotException {
        // Setup MQTT broker address/port details in application.json!

        // create wot
        Config config = ConfigFactory
                .parseString("wot.servient.servers = [\"city.sane.wot.binding.mqtt.MqttProtocolServer\"]")
                .withFallback(ConfigFactory.load());
        Wot wot = DefaultWot.serverOnly(config);

        Thing thing = new Thing.Builder()
                .setId("MQTT-Test")
                .setTitle("MQTT-Test")
                .setDescription("Tests a MQTT client that published counter values as an WoT event and subscribes the " +
                        "resetCounter topic as WoT action to reset the own counter.")
                .build();

        ExposedThing exposedThing = wot.produce(thing);

        AtomicInteger counter = new AtomicInteger();
        exposedThing
                .addAction("resetCounter", (input, options) -> {
                    System.out.println("Resetting counter");
                    counter.set(0);
                    return null;
                })
                .addEvent("counterEvent", new ThingEvent.Builder()
                        .setType("integer")
                        .build()
                );

        exposedThing.expose().whenComplete((result, ex) -> {
            System.out.println(exposedThing.getId() + " ready");

            System.out.println("=== TD ===");
            String json = exposedThing.toJson(true);
            System.out.println(json);
            System.out.println("==========");

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int newCount = counter.incrementAndGet();
                    exposedThing.getEvent("counterEvent").emit(newCount);
                    System.out.println("New count " + newCount);
                }
            }, 0, 1000);
        });
    }
}
