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
package city.sane.wot.binding.websocket.message;

import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WritePropertyTest {
    private WriteProperty message;

    @BeforeEach
    public void setUp() throws Exception {
        Content content = ContentManager.valueToContent(24);
        message = new WriteProperty("counter", "count", content);
    }

    @Test
    public void testConstructorNullParams() {
        assertThrows(NullPointerException.class, () -> new WriteProperty(null, null, null));
        assertThrows(NullPointerException.class, () -> new WriteProperty("counter", null, ContentManager.valueToContent(24)));
        assertThrows(NullPointerException.class, () -> new WriteProperty(null, "count", ContentManager.valueToContent(24)));
        assertThrows(NullPointerException.class, () -> new WriteProperty("counter", "count", null));
    }

    @Test
    public void getThingId() {
        assertEquals("counter", message.getThingId());
        assertNotNull(message.getThingId());
    }

    @Test
    public void getName() {
        assertEquals("count", message.getName());
        assertNotNull(message.getName());
    }

    @Test
    public void getPayload() throws ContentCodecException {
        assertEquals(ContentManager.valueToContent(24), message.getValue());
    }
}