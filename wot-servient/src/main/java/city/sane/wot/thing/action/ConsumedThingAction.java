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

import city.sane.Pair;
import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ConsumedThing;
import city.sane.wot.thing.ConsumedThingException;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Used in combination with {@link ConsumedThing} and allows consuming of a {@link ThingAction}.
 */
public class ConsumedThingAction<I, O> extends ThingAction<I, O> {
    private static final Logger log = LoggerFactory.getLogger(ConsumedThingAction.class);
    private final String name;
    private final ConsumedThing thing;

    public ConsumedThingAction(String name,
                               ThingAction<I, O> action,
                               ConsumedThing thing) {
        this.name = name;
        forms = action.getForms();
        input = action.getInput();
        output = action.getOutput();
        this.thing = thing;
    }

    /**
     * Invokes this action without parameters. Returns a future with the return result of the
     * action.
     *
     * @return
     */
    public CompletableFuture<O> invoke() {
        return invoke(Collections.emptyMap());
    }

    /**
     * Invokes this action and passes <code>parameters</codes> to it. Returns a future with the
     * return result of the action.
     *
     * @param parameters contains a map with the names of the uri variables as keys and
     *                   corresponding values (ex. <code>Map.of("step", 3)</code>).
     * @return
     */
    public CompletableFuture<O> invoke(Map<String, Object> parameters) {
        try {
            Pair<ProtocolClient, Form> clientAndForm = thing.getClientFor(getForms(), Operation.INVOKE_ACTION);
            ProtocolClient client = clientAndForm.first();
            Form form = clientAndForm.second();

            log.debug("Thing '{}' invoking Action '{}' with form '{}' and parameters '{}'", thing.getId(), name, form.getHref(), parameters);

            Content input = null;
            if (!parameters.isEmpty()) {
                input = ContentManager.valueToContent(parameters, form.getContentType());
            }

            form = ConsumedThing.handleUriVariables(form, parameters);

            CompletableFuture<Content> result = client.invokeResource(form, input);
            return result.thenApply(content -> {
                try {
                    return ContentManager.contentToValue(content, getOutput());
                }
                catch (ContentCodecException e) {
                    throw new CompletionException(new ConsumedThingException("Received invalid writeResource from Thing: " + e.getMessage()));
                }
            });
        }
        catch (ContentCodecException e) {
            throw new CompletionException(new ConsumedThingException("Received invalid input: " + e.getMessage()));
        }
        catch (ConsumedThingException e) {
            throw new CompletionException(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, thing);
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
        ConsumedThingAction<?, ?> that = (ConsumedThingAction<?, ?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(thing, that.thing);
    }

    @Override
    public String toString() {
        return "ConsumedThingAction{" +
                "name='" + name + '\'' +
                ", input=" + input +
                ", output=" + output +
                ", description='" + description + '\'' +
                ", descriptions=" + descriptions +
                ", forms=" + forms +
                ", uriVariables=" + uriVariables +
                '}';
    }
}
