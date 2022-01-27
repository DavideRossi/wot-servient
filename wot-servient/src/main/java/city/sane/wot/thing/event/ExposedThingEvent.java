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

import city.sane.wot.thing.ExposedThing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * Used in combination with {@link ExposedThing} and allows exposing of a {@link ThingEvent}.
 */
public class ExposedThingEvent<T> extends ThingEvent<T> {
    private static final Logger log = LoggerFactory.getLogger(ExposedThingEvent.class);
    private final String name;
    @JsonIgnore
    private final EventState<T> state;

    public ExposedThingEvent(String name, ThingEvent<T> event) {
        this(name, new EventState<>());
        description = event.getDescription();
        descriptions = event.getDescriptions();
        uriVariables = event.getUriVariables();
        type = event.getType();
        objectType = event.getObjectType();
        data = event.getData();
    }

    ExposedThingEvent(String name, EventState<T> state) {
        this.name = name;
        this.state = state;
    }

    public EventState<T> getState() {
        return state;
    }

    public void emit() {
        emit(null);
    }

    public void emit(T data) {
        log.debug("Event '{}' has been emitted", name);
        state.getSubject().onNext(Optional.ofNullable(data));
    }

    public Observable<Optional<T>> observer() {
        return state.getSubject();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ExposedThingEvent<?> that = (ExposedThingEvent<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(state, that.state);
    }

    @Override
    public String toString() {
        return "ExposedThingEvent{" +
                "name='" + name + '\'' +
                ", state=" + state +
                ", data=" + data +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", descriptions=" + descriptions +
                ", forms=" + forms +
                ", uriVariables=" + uriVariables +
                '}';
    }
}
