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

import city.sane.wot.thing.schema.ObjectSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonCodecTest {
    private JsonCodec codec;

    @BeforeEach
    public void setUp() {
        codec = new JsonCodec();
    }

    @Test
    public void bytesToValue() throws ContentCodecException {
        byte[] bytes = "{\"foo\":\"bar\"}".getBytes();
        Map value = codec.bytesToValue(bytes, new ObjectSchema());

        assertEquals("bar", value.get("foo"));
    }

    @Test
    public void valueToBytes() throws ContentCodecException {
        List<String> value = Arrays.asList("foo", "bar");
        byte[] bytes = codec.valueToBytes(value);

        assertEquals("[\"foo\",\"bar\"]", new String(bytes));
    }

    @Test
    public void mapToJsonToMap() throws ContentCodecException {
        Map<String, Object> value = Map.of(
                "foo", "bar",
                "etzala", Map.of("hello", "world")
        );
        byte[] bytes = codec.valueToBytes(value);
        Object newValue = codec.bytesToValue(bytes, new ObjectSchema());

        assertThat(newValue, instanceOf(Map.class));
    }
}