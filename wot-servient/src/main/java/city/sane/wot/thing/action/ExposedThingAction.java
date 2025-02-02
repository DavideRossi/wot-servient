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
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

/**
 * Used in combination with {@link ExposedThing} and allows exposing of a {@link ThingAction}.
 */
public class ExposedThingAction<I, O> extends ThingAction<I, O> {
    private static final Logger log = LoggerFactory.getLogger(ExposedThingAction.class);
    private final String name;
    private final ExposedThing thing;
    @JsonIgnore
    private final ActionState<I, O> state;

    public ExposedThingAction(String name, ThingAction<I, O> action, ExposedThing thing) {
        this(name, thing, new ActionState<>(), action.getDescription(), action.getDescriptions(), action.getUriVariables(), action.getObjectType(), action.getInput(), action.getOutput());
    }

    @SuppressWarnings("squid:S107")
    ExposedThingAction(String name,
                       ExposedThing thing,
                       ActionState<I, O> state,
                       String description,
                       Map<String, String> descriptions,
                       Map<String, Map<String, Object>> uriVariables,
                       String objectType,
                       DataSchema input,
                       DataSchema output) {
        this.name = name;
        this.thing = thing;
        this.state = state;
        this.description = description;
        this.descriptions = descriptions;
        this.uriVariables = uriVariables;
        this.objectType = objectType;
        this.input = input;
        this.output = output;
    }

    /**
     * Invokes the method and executes the handler defined in {@link #state}.
     *
     * @return
     */
    public CompletableFuture<O> invoke() {
        return invoke(null);
    }

    /**
     * Invokes the method and executes the handler defined in {@link #state}. <code>input</code>
     * contains the request payload.
     *
     * @param input
     * @return
     */
    public CompletableFuture<O> invoke(I input) {
        return invoke(input, Collections.emptyMap());
    }

    /**
     * Invokes the method and executes the handler defined in {@link #state}. <code>input</code>
     * contains the request payload. <code>options</code> can contain additional data (for example,
     * the query parameters when using COAP/HTTP).
     *
     * @param input
     * @param options
     * @return
     */
    public CompletableFuture<O> invoke(I input, Map<String, Map<String, Object>> options) {
        log.debug("'{}' has Action state of '{}': {}", thing.getId(), name, getState());

        if (getState().getHandler() != null) {
            log.debug("'{}' calls registered handler for Action '{}' with input '{}' and options '{}'", thing.getId(), name, input, options);
            try {
                CompletableFuture<O> output = getState().getHandler().apply(input, options);
                if (output == null) {
                    log.warn("'{}': Called registered handler for Action '{}' returned null. This can cause problems. Give Future with null result back.", thing.getId(), name);
                    output = completedFuture(null);
                }
                return output;
            }
            catch (Exception e) {
                return failedFuture(e);
            }
        }
        else {
            log.debug("'{}' has no handler for Action '{}'", thing.getId(), name);
            return completedFuture(null);
        }
    }

    public ActionState<I, O> getState() {
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
        ExposedThingAction<?, ?> that = (ExposedThingAction<?, ?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(thing, that.thing) &&
                Objects.equals(state, that.state);
    }

    @Override
    public String toString() {
        return "ExposedThingAction{" +
                "name='" + name + '\'' +
                ", state=" + state +
                ", input=" + input +
                ", output=" + output +
                ", description='" + description + '\'' +
                ", descriptions=" + descriptions +
                ", forms=" + forms +
                ", uriVariables=" + uriVariables +
                '}';
    }
}
