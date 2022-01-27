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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

/**
 * Serializes the single context or the list of contexts of a {@link Thing} to JSON. Is used by
 * Jackson
 */
class ContextSerializer extends JsonSerializer<Context> {
    @Override
    public void serialize(Context context,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        String defaultUrl = context.getDefaultUrl();
        Map<String, String> prefixedUrls = context.getPrefixedUrls();

        boolean hasDefaultUrl = defaultUrl != null;
        boolean hasPrefixedUrls = prefixedUrls.size() > 0;
        if (hasDefaultUrl && hasPrefixedUrls) {
            gen.writeStartArray();
        }

        if (hasDefaultUrl) {
            gen.writeString(defaultUrl);
        }

        if (hasPrefixedUrls) {
            gen.writeObject(prefixedUrls);
        }

        if (hasDefaultUrl && hasPrefixedUrls) {
            gen.writeEndArray();
        }
    }
}
