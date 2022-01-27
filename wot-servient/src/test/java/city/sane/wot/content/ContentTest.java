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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ContentTest {
    @Test
    public void testEquals() {
        Content contentA = new Content("application/json", "Hallo Welt".getBytes());
        Content contentB = new Content("application/json", "Hallo Welt".getBytes());

        assertEquals(contentA, contentB);
    }

    @Test
    public void testEqualsSameObject() {
        Content content = new Content("application/json", "Hallo Welt".getBytes());

        assertEquals(content, content);
    }

    @Test
    public void testEqualsNull() {
        Content content = new Content("application/json", "Hallo Welt".getBytes());

        assertNotEquals(null, content);
    }

    @Test
    public void testEqualsNoContent() {
        Content content = new Content("application/json", "Hallo Welt".getBytes());

        assertNotEquals("Hallo Welt", content);
    }

    @Test
    public void testHashCode() {
        Content contentA = new Content("application/json", "Hallo Welt".getBytes());
        Content contentB = new Content("application/json", "Hallo Welt".getBytes());

        assertEquals(contentA.hashCode(), contentB.hashCode());
    }
}
