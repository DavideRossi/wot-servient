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

import city.sane.wot.thing.schema.DataSchema;
import city.sane.wot.thing.schema.StringSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ThingActionTest {
    private DataSchema input;
    private DataSchema output;

    @BeforeEach
    public void setUp() {
        input = mock(DataSchema.class);
        output = mock(DataSchema.class);
    }

    @Test
    public void builder() {
        ThingAction<Object, Object> action = new ThingAction.Builder()
                .setInput(input)
                .setOutput(output)
                .build();

        assertEquals(input, action.getInput());
        assertEquals(output, action.getOutput());
    }

    @Test
    public void builderShouldUseStringAsDefaultInputSchema() {
        ThingAction<Object, Object> action = new ThingAction.Builder().build();

        assertEquals(new StringSchema(), action.getInput());
    }

    @Test
    public void builderShouldUseStringAsDefaultOutputSchema() {
        ThingAction<Object, Object> action = new ThingAction.Builder().build();

        assertEquals(new StringSchema(), action.getOutput());
    }
}