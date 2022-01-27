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

import java.util.Collections;
import java.util.Map;

/**
 * A ContentCodec is responsible for (de)serializing data in certain encoding (e.g. JSON, CBOR).
 */
interface ContentCodec {
    /**
     * Returns the media type supported by the codec (e.g. application/json).
     *
     * @return
     */
    String getMediaType();

    /**
     * Deserializes <code>body</code> according to the data schema defined in <code>schema</code>.
     *
     * @param body
     * @param schema
     * @param <T>
     * @return
     * @throws ContentCodecException
     */
    default <T> T bytesToValue(byte[] body, DataSchema<T> schema) throws ContentCodecException {
        return bytesToValue(body, schema, Collections.emptyMap());
    }

    /**
     * Deserializes <code>body</code> according to the data schema defined in <code>schema</code>.
     * <code>parameters</code> can contain additional information about the encoding of the data
     * (e.g. the used character set).
     *
     * @param body
     * @param schema
     * @param parameters
     * @param <T>
     * @return
     * @throws ContentCodecException
     */
    <T> T bytesToValue(byte[] body,
                       DataSchema<T> schema,
                       Map<String, String> parameters) throws ContentCodecException;

    /**
     * Serialized <code>value</code> according to the data schema defined in <code>schema</code> to
     * a byte array.
     *
     * @param value
     * @return
     * @throws ContentCodecException
     */
    default byte[] valueToBytes(Object value) throws ContentCodecException {
        return valueToBytes(value, Collections.emptyMap());
    }

    /**
     * Serialized <code>value</code> according to the data schema defined in <code>schema</code> to
     * a byte array. <code>parameters</code> can contain additional information about the encoding
     * of the data (e.g. the used character set).
     *
     * @param value
     * @param parameters
     * @return
     * @throws ContentCodecException
     */
    byte[] valueToBytes(Object value, Map<String, String> parameters) throws ContentCodecException;
}
