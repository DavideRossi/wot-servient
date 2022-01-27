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
package city.sane.wot.thing.action;

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
 * This class represents a read-only model of a thing action. The class {@link Builder} can be used
 * to build new thing action models. Used in combination with {@link city.sane.wot.thing.Thing}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThingAction<I, O> extends ThingInteraction<ThingAction<I, O>> {
    @JsonProperty("@type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String objectType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(as = VariableDataSchema.class)
    DataSchema<I> input;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(as = VariableDataSchema.class)
    DataSchema<O> output;

    ThingAction(String objectType, DataSchema<I> input, DataSchema<O> output) {
        this.objectType = objectType;
        this.input = input;
        this.output = output;
    }

    public ThingAction() {
    }

    public String getObjectType() {
        return objectType;
    }

    public DataSchema<I> getInput() {
        return input;
    }

    public DataSchema<O> getOutput() {
        return output;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), objectType, input, output);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ThingAction)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ThingAction<Object, Object> that = (ThingAction<Object, Object>) o;
        return Objects.equals(objectType, that.objectType) && Objects.equals(input, that.input) && Objects.equals(output, that.output);
    }

    @Override
    public String toString() {
        return "ThingAction{" +
                "objectType='" + objectType + '\'' +
                "input=" + input +
                ", output=" + output +
                ", description='" + description + '\'' +
                ", descriptions=" + descriptions +
                ", forms=" + forms +
                ", uriVariables=" + uriVariables +
                '}';
    }

    /**
     * Allows building new {@link ThingAction} objects.
     */
    public static class Builder extends AbstractBuilder<Builder> {
        private String objectType;
        private DataSchema input = new StringSchema();
        private DataSchema output = new StringSchema();

        public Builder setObjectType(String objectType) {
            this.objectType = objectType;
            return this;
        }

        public Builder setInput(DataSchema input) {
            this.input = input;
            return this;
        }

        public Builder setOutput(DataSchema output) {
            this.output = output;
            return this;
        }

        @Override
        public ThingAction<Object, Object> build() {
            ThingAction<Object, Object> action = new ThingAction<>(objectType, input, output);
            applyInteractionParameters(action);
            return action;
        }
    }
}
