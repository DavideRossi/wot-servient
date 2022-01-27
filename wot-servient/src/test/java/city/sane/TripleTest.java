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
package city.sane;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TripleTest {
    @Test
    public void first() {
        Triple pair = new Triple<>(10, false, "beers");

        assertEquals(10, pair.first());
    }

    @Test
    public void second() {
        Triple pair = new Triple<>(10, false, "beers");

        assertEquals(false, pair.second());
    }

    @Test
    public void third() {
        Triple pair = new Triple<>(10, false, "beers");

        assertEquals("beers", pair.third());
    }

    @Test
    public void testEquals() {
        Triple tripleA = new Triple(1, "cold", "beer");
        Triple tripleB = new Triple(1, "cold", "beer");
        Triple tripleC = new Triple(1, "warm", "beer");

        assertEquals(tripleA, tripleB);
        assertEquals(tripleB, tripleA);
        assertNotEquals(tripleA, tripleC);
        assertNotEquals(tripleC, tripleA);
    }
}
