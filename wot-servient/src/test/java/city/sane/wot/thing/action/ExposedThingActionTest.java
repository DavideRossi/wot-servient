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

import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.schema.DataSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExposedThingActionTest {
    private ExposedThing thing;
    private ActionState<Object, Object> state;
    private String description;
    private String objectType;
    private DataSchema input;
    private DataSchema output;
    private BiFunction handler;
    private Object invokeInput;
    private Map<String, Map<String, Object>> invokeOptions;

    @BeforeEach
    public void setUp() {
        thing = mock(ExposedThing.class);
        state = mock(ActionState.class);
        description = "";
        input = mock(DataSchema.class);
        output = mock(DataSchema.class);
        handler = mock(BiFunction.class);
        invokeInput = mock(Object.class);
        invokeOptions = mock(Map.class);
    }

    @Test
    public void invokeWithoutHandlerShouldReturnNullFuture() throws ExecutionException, InterruptedException {
        ExposedThingAction<Object, Object> exposedThingAction = new ExposedThingAction<Object, Object>("myAction", thing, state, description, Map.of(), Map.of(), objectType, input, output);
        exposedThingAction.invoke(invokeInput, invokeOptions);

        assertNull(exposedThingAction.invoke(invokeInput, invokeOptions).get());
    }

    @Test
    public void invokeWithHandlerShouldCallHandler() {
        when(state.getHandler()).thenReturn(handler);

        ExposedThingAction<Object, Object> exposedThingAction = new ExposedThingAction<Object, Object>("myAction", thing, state, description, Map.of(), Map.of(), objectType, input, output);
        exposedThingAction.invoke(invokeInput, invokeOptions);

        verify(handler).apply(invokeInput, invokeOptions);
    }

    @Test
    public void invokeWithBrokenHandlerShouldReturnFailedFuture() {
        when(handler.apply(any(), any())).thenThrow(new RuntimeException());
        when(state.getHandler()).thenReturn(handler);

        ExposedThingAction<Object, Object> exposedThingAction = new ExposedThingAction<Object, Object>("myAction", thing, state, description, Map.of(), Map.of(), objectType, input, output);
        assertThrows(ExecutionException.class, () -> exposedThingAction.invoke(invokeInput, invokeOptions).get());

        verify(handler).apply(invokeInput, invokeOptions);
    }

    @Test
    public void invokeShouldWrapNullResultsFromHandlerIntoFuture() {
        when(state.getHandler()).thenReturn(handler);
        when(handler.apply(any(), any())).thenReturn(null);

        ExposedThingAction<Object, Object> exposedThingAction = new ExposedThingAction<Object, Object>("myAction", thing, state, description, Map.of(), Map.of(), objectType, input, output);

        assertNotNull(exposedThingAction.invoke());
    }
}