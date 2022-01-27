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
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

/**
 * Used in combination with {@link ExposedThing} and allows exposing of a {@link ThingProperty}.
 */
public class ExposedThingProperty<T> extends ThingProperty<T> {
    private static final Logger log = LoggerFactory.getLogger(ExposedThingProperty.class);
    private final String name;
    private final ExposedThing thing;
    @JsonIgnore
    private final PropertyState<T> state;

    @SuppressWarnings("squid:S107")
    public ExposedThingProperty(String name,
                                ExposedThing thing,
                                PropertyState<T> state,
                                String objectType,
                                String description,
                                Map<String, String> descriptions,
                                String type,
                                boolean observable,
                                boolean readOnly,
                                boolean writeOnly,
                                Map<String, Map<String, Object>> uriVariables,
                                Map<String, Object> optionalProperties) {
        this.name = name;
        this.thing = thing;
        this.state = state;
        this.objectType = objectType;
        this.description = description;
        this.descriptions = descriptions;
        this.type = type;
        this.observable = observable;
        this.readOnly = readOnly;
        this.writeOnly = writeOnly;
        this.uriVariables = uriVariables;
        this.optionalProperties = optionalProperties;
    }

    public ExposedThingProperty(String name, ThingProperty<T> property, ExposedThing thing) {
        this.name = name;
        this.thing = thing;
        state = new PropertyState<>();

        if (property != null) {
            objectType = property.getObjectType();
            description = property.getDescription();
            descriptions = property.getDescriptions();
            type = property.getType();
            observable = property.isObservable();
            readOnly = property.isReadOnly();
            writeOnly = property.isWriteOnly();
            uriVariables = property.getUriVariables();
            optionalProperties = property.getOptionalProperties();
        }
    }

    public CompletableFuture<T> read() {
        // call read handler (if any)
        if (state.getReadHandler() != null) {
            log.debug("'{}' calls registered readHandler for Property '{}'", thing.getId(), name);

            // update internal state in case writeHandler wants to get the value
            try {
                return state.getReadHandler().get().whenComplete((customValue, e) -> state.setValue(customValue));
            }
            catch (Exception e) {
                return failedFuture(e);
            }
        }
        else {
            CompletableFuture<T> future = new CompletableFuture<>();

            T value = state.getValue();
            log.debug("'{}' gets internal value '{}' for Property '{}'", thing.getId(), value, name);
            future.complete(value);

            return future;
        }
    }

    public CompletableFuture<T> write(T value) {
        // call write handler (if any)
        if (state.getWriteHandler() != null) {
            log.debug("'{}' calls registered writeHandler for Property '{}'", thing.getId(), name);

            try {
                return state.getWriteHandler().apply(value).whenComplete((customValue, e) -> {
                    log.debug("'{}' write handler for Property '{}' sets custom value '{}'", thing.getId(), name, customValue);
                    if (!Objects.equals(state.getValue(), customValue)) {
                        state.setValue(customValue);

                        // inform property observers
                        state.getSubject().onNext(Optional.ofNullable(customValue));
                    }
                });
            }
            catch (Exception e) {
                return failedFuture(e);
            }
        }
        else {
            if (!Objects.equals(state.getValue(), value)) {
                log.debug("'{}' sets Property '{}' to internal value '{}'", thing.getId(), name, value);
                state.setValue(value);

                // inform property observers
                state.getSubject().onNext(Optional.ofNullable(value));
            }

            return completedFuture(null);
        }
    }

    public Observable<Optional<T>> observer() {
        return state.getSubject();
    }

    public PropertyState<T> getState() {
        return state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, thing, state);
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
        ExposedThingProperty<?> that = (ExposedThingProperty<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(thing, that.thing) &&
                Objects.equals(state, that.state);
    }

    @Override
    public String toString() {
        return "ExposedThingProperty{" +
                "name='" + name + '\'' +
                ", state=" + state +
                ", objectType='" + objectType + '\'' +
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
}
