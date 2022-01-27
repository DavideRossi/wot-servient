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
import city.sane.wot.thing.schema.DataSchema;
import city.sane.wot.thing.schema.IntegerSchema;
import city.sane.wot.thing.schema.NumberSchema;
import city.sane.wot.thing.schema.StringSchema;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * (De)serializes data in plaintext format.
 */
public class TextCodec implements ContentCodec {
    @Override
    public String getMediaType() {
        return "text/plain";
    }

    @Override
    public <T> T bytesToValue(byte[] body, DataSchema<T> schema, Map<String, String> parameters) {
        String charset = parameters.get("charset");

        String parsed;
        if (charset != null) {
            parsed = new String(body, Charset.forName(charset));
        }
        else {
            parsed = new String(body);
        }

        String type = schema.getType();
        // TODO: array, object
        switch (type) {
            case BooleanSchema
                    .TYPE:
                return (T) Boolean.valueOf(parsed);
            case IntegerSchema
                    .TYPE:
                return (T) Integer.valueOf(parsed);
            case NumberSchema
                    .TYPE:
                if (parsed.contains(".")) {
                    return (T) Double.valueOf(parsed);
                }
                else {
                    return (T) Long.valueOf(parsed);
                }
            case StringSchema
                    .TYPE:
                return (T) parsed;
            default:
                return null;
        }
    }

    @Override
    public byte[] valueToBytes(Object value, Map<String, String> parameters) {
        String charset = parameters.get("charset");

        if (charset != null) {
            return value.toString().getBytes(Charset.forName(charset));
        }
        else {
            return value.toString().getBytes();
        }
    }
}
