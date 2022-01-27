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

import city.sane.ObjectBuilder;

/**
 * Describes data whose type is determined at runtime.
 */
public class VariableDataSchema extends AbstractDataSchema<Object> {
    private String type;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Class getClassType() {
        switch (type) {
            case ArraySchema.TYPE:
                return ArraySchema.CLASS_TYPE;
            case BooleanSchema.TYPE:
                return BooleanSchema.CLASS_TYPE;
            case IntegerSchema.TYPE:
                return IntegerSchema.CLASS_TYPE;
            case NullSchema.TYPE:
                return NullSchema.CLASS_TYPE;
            case NumberSchema.TYPE:
                return NumberSchema.CLASS_TYPE;
            case ObjectSchema.TYPE:
                return ObjectSchema.CLASS_TYPE;
            default:
                return StringSchema.CLASS_TYPE;
        }
    }

    @Override
    public String toString() {
        return "VariableDataSchema{" +
                "type='" + type + '\'' +
                '}';
    }

    /**
     * Allows building new {@link VariableDataSchema} objects.
     */
    public static class Builder implements ObjectBuilder<VariableDataSchema> {
        private String type;

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        @Override
        public VariableDataSchema build() {
            VariableDataSchema schema = new VariableDataSchema();
            schema.type = type;
            return schema;
        }
    }
}
