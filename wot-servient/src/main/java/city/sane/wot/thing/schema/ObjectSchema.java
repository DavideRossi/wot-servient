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
package city.sane.wot.thing.schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Describes data of type <a href="https://www.w3.org/TR/wot-thing-description/#objectschema">object</a>.
 */
public class ObjectSchema extends AbstractDataSchema<Map> {
    public static final String TYPE = "object";
    public static final Class<Map> CLASS_TYPE = Map.class;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, DataSchema> properties;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<String> required;

    public ObjectSchema() {
        this(new HashMap<>(), new ArrayList<>());
    }

    public ObjectSchema(Map<String, DataSchema> properties,
                        List<String> required) {
        this.properties = properties;
        this.required = required;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Class<Map> getClassType() {
        return CLASS_TYPE;
    }

    @Override
    public String toString() {
        return "ObjectSchema{}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getProperties(), getRequired());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectSchema)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ObjectSchema that = (ObjectSchema) o;
        return Objects.equals(getProperties(), that.getProperties()) &&
                Objects.equals(getRequired(), that.getRequired());
    }

    public Map<String, DataSchema> getProperties() {
        return properties;
    }

    public List<String> getRequired() {
        return required;
    }
}
