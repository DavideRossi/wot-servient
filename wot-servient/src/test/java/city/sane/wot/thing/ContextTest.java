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
package city.sane.wot.thing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContextTest {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void fromJson() throws IOException {
        // single value
        assertEquals(
                new Context("http://www.w3.org/ns/td"),
                jsonMapper.readValue("\"http://www.w3.org/ns/td\"", Context.class)
        );

        // array
        assertEquals(
                new Context("http://www.w3.org/ns/td"),
                jsonMapper.readValue("[\"http://www.w3.org/ns/td\"]", Context.class)
        );

        // multi type array
        assertEquals(
                new Context("http://www.w3.org/ns/td").addContext("saref", "https://w3id.org/saref#"),
                jsonMapper.readValue("[\"http://www.w3.org/ns/td\",{\"saref\":\"https://w3id.org/saref#\"}]", Context.class)
        );
    }

    @Test
    public void toJson() throws JsonProcessingException {
        // single value
        assertEquals(
                "\"http://www.w3.org/ns/td\"",
                jsonMapper.writeValueAsString(new Context("http://www.w3.org/ns/td"))
        );

        // multi type array
        assertEquals(
                "[\"http://www.w3.org/ns/td\",{\"saref\":\"https://w3id.org/saref#\"}]",
                jsonMapper.writeValueAsString(new Context("http://www.w3.org/ns/td").addContext("saref", "https://w3id.org/saref#"))
        );
    }
}