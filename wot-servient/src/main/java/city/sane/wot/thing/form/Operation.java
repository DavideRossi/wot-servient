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
package city.sane.wot.thing.form;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the operation (e.g. read or write) on which a Thing Interaction is based.
 */
public enum Operation {
    // properties
    READ_PROPERTY("readproperty"),
    WRITE_PROPERTY("writeproperty"),
    OBSERVE_PROPERTY("observeproperty"),
    UNOBSERVE_PROPERTY("unobserveproperty"),
    READ_ALL_PROPERTIES("readallproperty"),
    READ_MULTIPLE_PROPERTIES("readmultipleproperty"),
    // events
    SUBSCRIBE_EVENT("subscribeevent"),
    UNSUBSCRIBE_EVENT("unsubscribeevent"),
    // actions
    INVOKE_ACTION("invokeaction");
    private static final Map<String, Operation> LOOKUP = new HashMap<>();

    static {
        for (Operation env : Operation.values()) {
            LOOKUP.put(env.toJsonValue(), env);
        }
    }

    private final String tdValue;

    Operation(String tdValue) {
        this.tdValue = tdValue;
    }

    @JsonValue
    private String toJsonValue() {
        return tdValue;
    }

    @JsonCreator
    public static Operation fromJsonValue(String jsonValue) {
        return LOOKUP.get(jsonValue);
    }
}
