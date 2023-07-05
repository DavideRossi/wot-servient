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
package city.sane.wot.thing.property;

import city.sane.wot.thing.ThingInteraction;
import city.sane.wot.thing.schema.DataSchema;
import city.sane.wot.thing.schema.VariableDataSchema;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a read-only model of a thing property. The class {@link Builder} can be
 * used to build new thing property models. Used in combination with {@link
 * city.sane.wot.thing.Thing}
 */
public class ThingProperty<T> extends ThingInteraction<ThingProperty<T>> implements DataSchema<T> {
    @JsonProperty("@type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String objectType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    boolean observable;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    boolean readOnly;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    boolean writeOnly;
    Map<String, Object> optionalProperties = new HashMap<>();
    private final Map<String, String> jsonschemaMap = Map.of(
        "jsonschema:ArraySchema", "array", 
        "jsonschema:IntegerSchema", "integer", 
        "jsonschema:NullSchema", "null", 
        "jsonschema:ObjectSchema", "object",
        "jsonschema:StringSchema", "string", 
        "jsonschema:BooleanSchema", "boolean", 
        "jsonschema:NumberSchema", "number");

    public String getObjectType() {
        return objectType;
    }

    @Override
    public String getType() {
        //TODO: horrible hack to support properties missing type while using @type with jsonschemas
        if(this.type == null && this.objectType != null) {
            return jsonschemaMap.get(this.objectType);
        }
        return type;
    }

    @Override
    public Class<T> getClassType() {
        return new VariableDataSchema.Builder().setType(type).build().getClassType();
    }

    public boolean isObservable() {
        return observable;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isWriteOnly() {
        return writeOnly;
    }

    @JsonAnyGetter
    public Map<String, Object> getOptionalProperties() {
        return optionalProperties;
    }

    public Object getOptional(String name) {
        return optionalProperties.get(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), objectType, type, observable, readOnly, writeOnly, optionalProperties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ThingProperty)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ThingProperty<Object> that = (ThingProperty<Object>) o;
        return observable == that.observable &&
                readOnly == that.readOnly &&
                writeOnly == that.writeOnly &&
                Objects.equals(objectType, that.objectType) &&
                Objects.equals(type, that.type) &&
                Objects.equals(optionalProperties, that.optionalProperties);
    }

    @Override
    public String toString() {
        return "ThingProperty{" +
                "objectType='" + objectType + '\'' +
                ", type='" + type + '\'' +
                ", observable=" + observable +
                ", readOnly=" + readOnly +
                ", writeOnly=" + writeOnly +
                ", optionalProperties=" + optionalProperties +
                ", description='" + description + '\'' +
                ", descriptions=" + descriptions +
                ", forms=" + forms +
                ", uriVariables=" + uriVariables +
                '}';
    }

    /**
     * Allows building new {@link ThingProperty} objects.
     */
    public static class Builder extends AbstractBuilder<Builder> {
        private String objectType;
        private String type = "string";
        private boolean observable;
        private boolean readOnly;
        private boolean writeOnly;
        private Map<String, Object> optionalProperties = new HashMap<>();

        public Builder setObjectType(String objectType) {
            this.objectType = objectType;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setObservable(boolean observable) {
            this.observable = observable;
            return this;
        }

        public Builder setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Builder setWriteOnly(boolean writeOnly) {
            this.writeOnly = writeOnly;
            return this;
        }

        public Builder setOptionalProperties(Map<String, Object> optionalProperties) {
            this.optionalProperties = optionalProperties;
            return this;
        }

        @JsonAnySetter
        public Builder setOptional(String name, String value) {
            optionalProperties.put(name, value);
            return this;
        }

        @Override
        public ThingProperty<Object> build() {
            ThingProperty<Object> property = new ThingProperty<>();
            property.objectType = objectType;
            property.type = type;
            property.observable = observable;
            property.readOnly = readOnly;
            property.writeOnly = writeOnly;
            property.optionalProperties = optionalProperties;
            applyInteractionParameters(property);
            return property;
        }
    }
}
