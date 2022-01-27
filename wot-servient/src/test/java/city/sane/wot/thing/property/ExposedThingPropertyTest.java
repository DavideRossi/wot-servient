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

import city.sane.wot.thing.ExposedThing;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExposedThingPropertyTest {
    private ExposedThing thing;
    private String name;
    private ThingProperty<Object> property;
    private PropertyState<Object> state;
    private String objectType;
    private String description;
    private Map<String, String> descriptions;
    private String type;
    private boolean observable;
    private boolean readOnly;
    private boolean writeOnly;
    private Map<String, Map<String, Object>> uriVariables;
    private Map<String, Object> optionalProperties;
    private ExposedThingProperty<Object> exposedProperty;
    private Supplier<CompletableFuture<Object>> readHandler;
    private Subject subject;
    private Function<Object, CompletableFuture<Object>> writeHandler;

    @BeforeEach
    public void setUp() {
        thing = mock(ExposedThing.class);
        name = "foo";
        property = mock(ThingProperty.class);
        state = mock(PropertyState.class);
        readHandler = mock(Supplier.class);
        writeHandler = mock(Function.class);
        subject = mock(Subject.class);
    }

    @Test
    public void readWithoutHandlerShouldReturnStateValue() {
        exposedProperty = new ExposedThingProperty<>(name, thing, state, objectType, description, descriptions, type, observable, readOnly, writeOnly, uriVariables, optionalProperties);

        exposedProperty.read();

        verify(state).getValue();
    }

    @Test
    public void readWithHandlerShouldCallHandler() {
        when(state.getReadHandler()).thenReturn(readHandler);

        exposedProperty = new ExposedThingProperty<>(name, thing, state, objectType, description, descriptions, type, observable, readOnly, writeOnly, uriVariables, optionalProperties);

        exposedProperty.read();

        verify(readHandler).get();
    }

    @Test
    public void readWithBrokenHandlerShouldReturnFailedFuture() {
        when(readHandler.get()).thenThrow(new RuntimeException());
        when(state.getReadHandler()).thenReturn(readHandler);

        exposedProperty = new ExposedThingProperty<>(name, thing, state, objectType, description, descriptions, type, observable, readOnly, writeOnly, uriVariables, optionalProperties);

        assertThrows(ExecutionException.class, () -> exposedProperty.read().get());
    }

    @Test
    public void writeWithoutHandlerShouldSetStateValueAndInformSubject() {
        when(state.getSubject()).thenReturn(subject);

        exposedProperty = new ExposedThingProperty<>(name, thing, state, objectType, description, descriptions, type, observable, readOnly, writeOnly, uriVariables, optionalProperties);

        exposedProperty.write(1337);

        verify(state).setValue(1337);
        verify(subject).onNext(Optional.of(1337));
    }

    @Test
    public void writeWithHandlerShouldCallHandlerAndInformSubject() {
        when(writeHandler.apply(any())).thenReturn(completedFuture(1337));
        when(state.getWriteHandler()).thenReturn(writeHandler);
        when(state.getSubject()).thenReturn(subject);

        exposedProperty = new ExposedThingProperty<>(name, thing, state, objectType, description, descriptions, type, observable, readOnly, writeOnly, uriVariables, optionalProperties);

        exposedProperty.write(1337);

        verify(writeHandler).apply(1337);
        verify(subject).onNext(Optional.of(1337));
    }
}