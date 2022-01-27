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

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExposedThingEventTest {
    private String name;
    private EventState state;
    private Subject subject;
    private Observer observer;

    @BeforeEach
    public void setUp() {
        name = "change";
        state = mock(EventState.class);
        subject = mock(Subject.class);
        observer = mock(Observer.class);
    }

    @Test
    public void emitWithoutDataShouldEmitNullAsNextValueToEventState() {
        when(state.getSubject()).thenReturn(subject);

        ExposedThingEvent event = new ExposedThingEvent<>(name, state);
        event.emit();

        verify(subject).onNext(Optional.empty());
    }

    @Test
    public void emitShouldEmitGivenDataAsNextValueToEventState() {
        when(state.getSubject()).thenReturn(subject);

        ExposedThingEvent event = new ExposedThingEvent<>(name, state);
        event.emit("Hallo Welt");

        verify(subject).onNext(Optional.of("Hallo Welt"));
    }

    @Test
    public void subscribeShouldSubscribeToEventState() {
        when(state.getSubject()).thenReturn(subject);

        ExposedThingEvent event = new ExposedThingEvent<>(name, state);
        event.observer().subscribe(observer);

        verify(subject).subscribe(observer);
    }
}