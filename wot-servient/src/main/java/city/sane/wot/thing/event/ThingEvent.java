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
package city.sane.wot.thing.event;

import city.sane.wot.thing.ThingInteraction;
import city.sane.wot.thing.schema.DataSchema;
import city.sane.wot.thing.schema.StringSchema;
import city.sane.wot.thing.schema.VariableDataSchema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

/**
 * This class represents a read-only model of a thing event. The class {@link Builder} can be used
 * to build new thing event models. Used in combination with {@link city.sane.wot.thing.Thing}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThingEvent<T> extends ThingInteraction<ThingEvent<T>> {
    @JsonProperty("@type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String objectType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(as = VariableDataSchema.class)
    DataSchema<T> data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;

    public String getObjectType() {
        return objectType;
    }

    public String getType() {
        return type;
    }

    public DataSchema<T> getData() {
        return data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), objectType, data, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ThingEvent)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ThingEvent<Object> that = (ThingEvent<Object>) o;
        return Objects.equals(objectType, that.objectType) && Objects.equals(data, that.data) && Objects.equals(type, that.type);
    }

    @Override
    public String toString() {
        return "ThingEvent{" +
                "objectType='" + objectType + '\'' +
                "data=" + data +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", descriptions=" + descriptions +
                ", forms=" + forms +
                ", uriVariables=" + uriVariables +
                '}';
    }

    /**
     * Allows building new {@link ThingEvent} objects.
     */
    public static class Builder extends AbstractBuilder<Builder> {
        private String objectType;
        private DataSchema data = new StringSchema();
        private String type;

        public Builder setObjectType(String objectType) {
            this.objectType = objectType;
            return this;
        }

        public ThingEvent.Builder setData(DataSchema data) {
            this.data = data;
            return this;
        }

        public ThingEvent.Builder setType(String type) {
            this.type = type;
            return this;
        }

        @Override
        public ThingEvent<Object> build() {
            ThingEvent<Object> event = new ThingEvent<>();
            event.objectType = objectType;
            event.data = data;
            event.type = type;
            applyInteractionParameters(event);
            return event;
        }
    }
}
