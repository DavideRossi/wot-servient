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
package city.sane.wot.thing

import city.sane.wot.thing.ExposedThing
import city.sane.wot.thing.action.ThingAction
import city.sane.wot.thing.event.ThingEvent
import city.sane.wot.thing.property.ThingProperty
import com.fasterxml.jackson.databind.ObjectMapper

import java.util.concurrent.CompletableFuture
import java.util.function.BiFunction

class ExposedThingExtensions {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()

    static ExposedThing addProperty(ExposedThing self, String name, Map map) {
        def property = JSON_MAPPER.convertValue(map, ThingProperty.class)
        return self.addProperty(name, property)
    }

    static ExposedThing addProperty(ExposedThing self, String name, Map map, Object init) {
        def property = JSON_MAPPER.convertValue(map, ThingProperty.class)
        return self.addProperty(name, property, init)
    }

    static ExposedThing addAction(ExposedThing self, String name, Map map, BiFunction<Object, Map<String, Object>, CompletableFuture<Object>> handler) {
        def action = JSON_MAPPER.convertValue(map, ThingAction.class)
        return self.addAction(name, action, handler)
    }

    static ExposedThing addEvent(ExposedThing self, String name, Map map) {
        def event = JSON_MAPPER.convertValue(map, ThingEvent.class)
        return self.addEvent(name, event)
    }
}
