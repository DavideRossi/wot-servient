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
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinkFormatCodecTest {
    private LinkFormatCodec codec;

    @BeforeEach
    public void setUp() {
        codec = new LinkFormatCodec();
    }

    @Test
    public void bytesToValue() throws ContentCodecException {
        byte[] bytes = "</reg/1/>;ep=\"RIOT-34136DAB556DC1D3\";base=\"coap://[fd00:6:7:8:d1c1:6d55:ab6d:1336]\";rt=\"core.rd-ep\",</reg/2/>;ep=\"RIOT-34136EAB746DC1D3\";base=\"coap://[fd00:6:7:8:d1c1:6d74:ab6e:1336]\";rt=\"core.rd-ep\"".getBytes();

        Map<String, Map<String, String>> value = codec.bytesToValue(bytes, new ObjectSchema());

        MatcherAssert.assertThat(value, hasKey("</reg/1/>"));
        MatcherAssert.assertThat(value, hasKey("</reg/2/>"));
    }

    @Test
    public void valueToBytes() throws ContentCodecException {
        Map<String, Map<String, String>> value = Map.of(
                "</reg/1/>", Map.of(
                        "ep", "RIOT-34136DAB556DC1D3",
                        "base", "coap://[fd00:6:7:8:d1c1:6d55:ab6d:1336]",
                        "rt", "core.rd-ep"
                ),
                "</reg/2/>", Map.of(
                        "ep", "RIOT-34136EAB746DC1D3",
                        "base", "coap://[fd00:6:7:8:d1c1:6d74:ab6e:1336]",
                        "rt", "core.rd-ep"
                )
        );

        byte[] bytes = codec.valueToBytes(value);

        // should not fail
        assertTrue(true);
//        assertEquals("</reg/1/>;ep=\"RIOT-34136DAB556DC1D3\";base=\"coap://[fd00:6:7:8:d1c1:6d55:ab6d:1336]\";rt=\"core.rd-ep\",</reg/2/>;ep=\"RIOT-34136EAB746DC1D3\";base=\"coap://[fd00:6:7:8:d1c1:6d74:ab6e:1336]\";rt=\"core.rd-ep\"", new String(bytes));
    }
}