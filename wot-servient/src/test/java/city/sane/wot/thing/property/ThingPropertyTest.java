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
package city.sane.wot.thing.property;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThingPropertyTest {
    @Test
    public void testEquals() {
        ThingProperty<Object> property1 = new ThingProperty<Object>();
        ThingProperty<Object> property2 = new ThingProperty<Object>();

        assertEquals(property1, property2);
    }

    @Test
    public void builder() {
        ThingProperty<Object> property = new ThingProperty.Builder()
                .setObjectType("saref:Temperature")
                .setType("integer")
                .setObservable(true)
                .setReadOnly(true)
                .setWriteOnly(false)
                .setOptionalProperties(Map.of("foo", "bar"))
                .build();

        assertEquals("saref:Temperature", property.getObjectType());
        assertEquals("integer", property.getType());
        assertTrue(property.isObservable());
        assertTrue(property.isReadOnly());
        assertFalse(property.isWriteOnly());
        assertEquals(Map.of("foo", "bar"), property.getOptionalProperties());
        assertEquals("bar", property.getOptional("foo"));
    }

    @Test
    public void builderShouldUseStringAsDefaultType() {
        ThingProperty<Object> property = new ThingProperty.Builder().build();

        assertEquals("string", property.getType());
        assertEquals(String.class, property.getClassType());
    }
}