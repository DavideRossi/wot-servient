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
package city.sane.wot.content;

import city.sane.wot.thing.schema.BooleanSchema;
import city.sane.wot.thing.schema.IntegerSchema;
import city.sane.wot.thing.schema.NullSchema;
import city.sane.wot.thing.schema.NumberSchema;
import city.sane.wot.thing.schema.StringSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextCodecTest {
    private TextCodec codec;

    @BeforeEach
    public void setUp() {
        codec = new TextCodec();
    }

    @Test
    public void bytesToBooleanValue() throws ContentCodecException {
        byte[] bytes = "true".getBytes();
        Object value = codec.bytesToValue(bytes, new BooleanSchema());

        boolean bool = (boolean) value;

        assertTrue(bool);
    }

    @Test
    public void bytesToIntegerValue() throws ContentCodecException {
        byte[] bytes = "1337".getBytes();
        Object value = codec.bytesToValue(bytes, new IntegerSchema());

        int integer = (int) value;

        assertEquals(1337, integer);
    }

    @Test
    public void bytesToNullValue() throws ContentCodecException {
        byte[] bytes = "null".getBytes();
        Object value = codec.bytesToValue(bytes, new NullSchema());

        assertNull(value);
    }

    @Test
    public void bytesToNumberFloatValue() throws ContentCodecException {
        byte[] bytes = "13.37".getBytes();
        Number value = codec.bytesToValue(bytes, new NumberSchema());

        assertNotNull(value, "Should be instance of Number");

        assertEquals(13.37, value);
    }

    @Test
    public void bytesToNumberLongValue() throws ContentCodecException {
        byte[] bytes = "1337".getBytes();
        Number value = codec.bytesToValue(bytes, new NumberSchema());

        assertNotNull(value, "Should be instance of Number");

        assertEquals(1337L, value);
    }

    @Test
    public void bytesToStringValue() throws ContentCodecException {
        byte[] bytes = "Hallo Welt".getBytes();
        String value = codec.bytesToValue(bytes, new StringSchema());

        assertNotNull(value, "Should be instance of String");

        assertEquals("Hallo Welt", value);
    }

    @Test
    public void valueToBytes() throws ContentCodecException {
        String value = "Hallo Welt";
        byte[] bytes = codec.valueToBytes(value);

        assertArrayEquals("Hallo Welt".getBytes(), bytes);
    }
}