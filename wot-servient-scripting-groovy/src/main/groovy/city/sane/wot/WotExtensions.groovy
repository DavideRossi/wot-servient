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
package city.sane.wot


import city.sane.wot.Wot
import city.sane.wot.thing.ConsumedThing
import city.sane.wot.thing.ExposedThing
import city.sane.wot.thing.Thing
import com.fasterxml.jackson.databind.ObjectMapper

class WotExtensions {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()

    static ExposedThing produce(Wot self, Map map) {
        def thing = JSON_MAPPER.convertValue(map, Thing.class)
        return self.produce(thing)
    }

    static ConsumedThing consume(Wot self, Map map) {
        def thing = JSON_MAPPER.convertValue(map, Thing.class)
        return self.consume(thing)
    }
}