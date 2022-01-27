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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Deserializes the individual context or the list of contexts of a {@link Thing} from JSON. Is used
 * by Jackson
 */
class ContextDeserializer extends JsonDeserializer<Context> {
    private static final Logger log = LoggerFactory.getLogger(ContextDeserializer.class);

    @Override
    public Context deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            return new Context(p.getValueAsString());
        }
        else if (t == JsonToken.START_ARRAY) {
            Context context = new Context();

            ArrayNode arrayNode = p.getCodec().readTree(p);
            Iterator<JsonNode> arrayElements = arrayNode.elements();
            while (arrayElements.hasNext()) {
                JsonNode arrayElement = arrayElements.next();

                if (arrayElement instanceof TextNode) {
                    context.addContext(arrayElement.asText());
                }
                else if (arrayElement instanceof ObjectNode) {
                    Iterator<Map.Entry<String, JsonNode>> objectEntries = arrayElement.fields();
                    while (objectEntries.hasNext()) {
                        Map.Entry<String, JsonNode> objectEntry = objectEntries.next();
                        String prefix = objectEntry.getKey();
                        String url = objectEntry.getValue().asText();
                        context.addContext(prefix, url);
                    }
                }
            }

            return context;
        }
        else {
            log.warn("Unable to deserialize Context of type '{}'", t);
            return null;
        }
    }
}
