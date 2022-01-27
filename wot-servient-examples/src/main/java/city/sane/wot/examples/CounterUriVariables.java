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
import city.sane.wot.thing.property.ThingProperty;
import city.sane.wot.thing.schema.ObjectSchema;

import java.util.Map;

/**
 * Produces and exposes a counter thing with variables for interaction.
 */
@SuppressWarnings({ "java:S106", "java:S1192" })
class CounterUriVariables {
    public static void main(String[] args) throws WotException {
        // create wot
        Wot wot = new DefaultWot();

        // create counter
        Thing thing = new Thing.Builder()
                .setId("counter")
                .setTitle("counter")
                .setDescription("counter example Thing")
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

        exposedThing.addAction("increment",
                new ThingAction.Builder()
                        .setDescription("Incrementing counter value with optional step value as uriVariable")
                        .setUriVariables(Map.of(
                                "step", Map.of(
                                        "type", "integer",
                                        "minimum", 1,
                                        "maximum", 250
                                )
                        ))
                        .setInput(new ObjectSchema())
                        .build(),
                (input, options) -> {
                    System.out.println("CounterUriVariables: Incrementing, input= " + input + ", options= " + options);
                    exposedThing.getProperty("count").read().thenApply(value -> {
                        int step;
                        if (input != null && ((Map) input).containsKey("step")) {
                            step = (Integer) ((Map) input).get("step");
                        }
                        else if (options.containsKey("uriVariables") && options.get("uriVariables").containsKey("step")) {
                            step = (int) options.get("uriVariables").get("step");
                        }
                        else {
                            step = 1;
                        }

                        int newValue = ((Integer) value) + step;
                        exposedThing.getProperty("count").write(newValue);
                        return newValue;
                    });
                }
        );

        exposedThing.addAction("decrement",
                new ThingAction.Builder()
                        .setDescription("Decrementing counter value with optional step value as uriVariable")
                        .setUriVariables(Map.of(
                                "step", Map.of(
                                        "type", "integer",
                                        "minimum", 1,
                                        "maximum", 250
                                )
                        ))
                        .build(),
                (input, options) -> {
                    System.out.println("CounterUriVariables: Decrementing, input= " + input + ", options= " + options);
                    exposedThing.getProperty("count").read().thenApply(value -> {
                        int step = 1;
                        if (options.get("uriVariables") != null && options.get("uriVariables").get("step") != null) {
                            step = (int) options.get("uriVariables").get("step");
                        }
                        int newValue = ((Integer) value) - step;
                        exposedThing.getProperty("count").write(newValue);
                        return newValue;
                    });
                }
        );

        exposedThing.expose();
    }
}
