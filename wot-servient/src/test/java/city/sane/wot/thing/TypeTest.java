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
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeTest {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void fromJson() throws IOException {
        // single value
        assertEquals(
                new Type("Thing"),
                jsonMapper.readValue("\"Thing\"", Type.class)
        );

        // array
        assertEquals(
                new Type("Thing").addType("saref:LightSwitch"),
                jsonMapper.readValue("[\"Thing\",\"saref:LightSwitch\"]", Type.class)
        );
    }

    @Test
    public void toJson() throws JsonProcessingException {
        // single value
        assertEquals(
                "\"Thing\"",
                jsonMapper.writeValueAsString(new Type("Thing"))
        );

        // multi type array
        assertThatJson(jsonMapper.writeValueAsString(new Type("Thing").addType("saref:LightSwitch")))
                .when(Option.IGNORING_ARRAY_ORDER)
                .isArray()
                .contains("Thing", "saref:LightSwitch");
    }
}