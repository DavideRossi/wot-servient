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

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormTest {
    @Test
    public void builder() {
        Form form = new Form.Builder()
                .setHref("test:/foo")
                .setOp(Operation.OBSERVE_PROPERTY)
                .setSubprotocol("longpolling")
                .setContentType("application/json")
                .setOptionalProperties(Map.of("foo", "bar"))
                .build();

        assertEquals("test:/foo", form.getHref());
        assertEquals(Collections.singletonList(Operation.OBSERVE_PROPERTY), form.getOp());
        assertEquals("longpolling", form.getSubprotocol());
        assertEquals("application/json", form.getContentType());
        assertEquals(Map.of("foo", "bar"), form.getOptionalProperties());
        assertEquals("bar", form.getOptional("foo"));
    }
}