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
import city.sane.wot.thing.action.ThingAction;
import city.sane.wot.thing.event.ThingEvent;
import city.sane.wot.thing.schema.VariableDataSchema;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produces and exposes a thing that will fire an event every few seconds.
 */
@SuppressWarnings({ "java:S106", "java:S112" })
class ExampleEvent {
    public static void main(String[] args) throws WotException {
        // create wot
        Wot wot = new DefaultWot();

        // create counter
        Thing thing = new Thing.Builder()
                .setId("EventSource")
                .setTitle("EventSource")
                .build();
        ExposedThing exposedThing = wot.produce(thing);

        System.out.println("Produced " + exposedThing.getId());

        AtomicInteger counter = new AtomicInteger();
        exposedThing.addAction("reset", new ThingAction<>(),
                () -> {
                    System.out.println("Resetting");
                    counter.set(0);
                }
        );
        exposedThing.addEvent("onchange",
                new ThingEvent.Builder()
                        .setData(new VariableDataSchema.Builder()
                                .setType("integer")
                                .build()
                        ).build()
        );

        exposedThing.expose().whenComplete((result, e) -> {
            if (result != null) {
                System.out.println(exposedThing + " ready");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int newCount = counter.incrementAndGet();
                        exposedThing.getEvent("onchange").emit(newCount);
                        System.out.println("Emitted change " + newCount);
                    }
                }, 0, 5000);
            }
            else {
                throw new RuntimeException("Expose error: " + e.toString());
            }
        });
    }
}
