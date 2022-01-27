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

import city.sane.wot.thing.schema.DataSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * (De)serializes data in JSON format.
 */
public class JsonCodec implements ContentCodec {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getMediaType() {
        return "application/json";
    }

    @Override
    public <T> T bytesToValue(byte[] body,
                              DataSchema<T> schema,
                              Map<String, String> parameters) throws ContentCodecException {
        try {
            return getMapper().readValue(body, schema.getClassType());
        }
        catch (IOException e) {
            throw new ContentCodecException("Failed to decode " + getMediaType() + ": " + e.toString());
        }
    }

    @Override
    public byte[] valueToBytes(Object value,
                               Map<String, String> parameters) throws ContentCodecException {
        try {
            return getMapper().writeValueAsBytes(value);
        }
        catch (JsonProcessingException e) {
            throw new ContentCodecException("Failed to encode " + getMediaType() + ": " + e.toString());
        }
    }

    ObjectMapper getMapper() {
        return mapper;
    }
}
