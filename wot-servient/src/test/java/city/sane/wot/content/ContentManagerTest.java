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

import city.sane.wot.thing.schema.NumberSchema;
import city.sane.wot.thing.schema.ObjectSchema;
import city.sane.wot.thing.schema.StringSchema;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentManagerTest {
    @Test
    public void contentToValueShouldUseFallbackDeserializerForUnsupportedFormats() throws ContentCodecException, IOException {
        // serialize 42
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(42);
        oos.flush();
        byte[] body = bos.toByteArray();

        Content content = new Content("none/none", body);
        Object value = ContentManager.contentToValue(content, new NumberSchema());

        assertEquals(42, value);
    }

    @Test
    public void contentToValueObject() throws ContentCodecException {
        Content content = new Content("application/json", "{\"foo\":\"bar\"}".getBytes());
        Map value = ContentManager.contentToValue(content, new ObjectSchema());

        assertEquals("bar", value.get("foo"));
    }

    @Test
    public void contentToValueWithMediaTypeParameters() throws ContentCodecException {
        Content content = new Content("text/plain; charset=utf-8", "Hello World".getBytes());
        String value = ContentManager.contentToValue(content, new StringSchema());

        assertEquals("Hello World", value);
    }

    @Test
    public void contentToValueBrokenByteArray() {
        Content content = new Content("application/xml", new byte[]{ 0x4f });

        assertThrows(ContentCodecException.class, () -> ContentManager.contentToValue(content, new StringSchema()));
    }

    @Test
    public void valueToContentWithUnsupportedFormat() throws ContentCodecException, IOException, ClassNotFoundException {
        Content content = ContentManager.valueToContent(42, "none/none");

        // deserialize byte array
        ByteArrayInputStream bis = new ByteArrayInputStream(content.getBody());
        ObjectInputStream ois = new ObjectInputStream(bis);

        assertEquals(42, ois.readObject());
    }

    @Test
    public void removeCodec() {
        try {
            assertTrue(ContentManager.isSupportedMediaType("text/plain"));
            ContentManager.removeCodec("text/plain");
            assertFalse(ContentManager.isSupportedMediaType("text/plain"));
        }
        finally {
            ContentManager.addCodec(new TextCodec());
        }
    }
}