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
import city.sane.wot.thing.property.ThingProperty;

import java.util.Date;

/**
 * Produces and exposes a counter thing.
 */
@SuppressWarnings({ "squid:S106", "java:S1192" })
class Counter {
    public static void main(String[] args) throws WotException {
        // create wot
        Wot wot = new DefaultWot();

        // create counter
        Thing thing = new Thing.Builder()
                .setId("counter")
                .setTitle("counter")
                .build();
        ExposedThing exposedThing = wot.produce(thing);

        System.out.println("Produced " + exposedThing.getId());

        // expose counter
        exposedThing.addProperty("count",
                new ThingProperty.Builder()
                        .setType("integer")
                        .setDescription("current counter value")
                        .setObservable(true)
                        .setReadOnly(true)
                        .build(),
                42);

        exposedThing.addProperty("lastChange",
                new ThingProperty.Builder()
                        .setType("string")
                        .setDescription("last change of counter value")
                        .setObservable(true)
                        .setReadOnly(true)
                        .build(),
                new Date().toString());

        exposedThing.addAction("increment", new ThingAction<>(),
                () -> exposedThing.getProperty("count").read().thenApply(value -> {
                    int newValue = ((Integer) value) + 1;
                    exposedThing.getProperty("count").write(newValue);
                    exposedThing.getProperty("lastChange").write(new Date().toString());
                    exposedThing.getEvent("change").emit();
                    return newValue;
                })
        );

        exposedThing.addAction("decrement", new ThingAction<>(),
                () -> exposedThing.getProperty("count").read().thenApply(value -> {
                    int newValue = ((Integer) value) - 1;
                    exposedThing.getProperty("count").write(newValue);
                    exposedThing.getProperty("lastChange").write(new Date().toString());
                    exposedThing.getEvent("change").emit();
                    return newValue;
                })
        );

        exposedThing.addAction("reset", new ThingAction<>(),
                () -> exposedThing.getProperty("count").write(0).whenComplete((value, e) -> {
                    exposedThing.getProperty("lastChange").write(new Date().toString());
                    exposedThing.getEvent("change").emit();
                })
        );

        exposedThing.addEvent("change", new ThingEvent<>());

        exposedThing.expose().whenComplete((r, e) -> {
            if (e == null) {
                System.out.println(exposedThing.getId() + " ready");
            }
            else {
                System.err.println(e.getMessage());
            }
        });
    }
}
