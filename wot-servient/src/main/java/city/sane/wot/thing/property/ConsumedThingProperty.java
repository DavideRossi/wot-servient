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

import city.sane.Pair;
import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ConsumedThing;
import city.sane.wot.thing.ConsumedThingException;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 * Used in combination with {@link ConsumedThing} and allows consuming of a {@link ThingProperty}.
 */
public class ConsumedThingProperty<T> extends ThingProperty<T> {
    private static final Logger log = LoggerFactory.getLogger(ConsumedThingProperty.class);
    private final String name;
    private final ConsumedThing thing;

    public ConsumedThingProperty(String name, ThingProperty<T> property, ConsumedThing thing) {
        this.name = name;

        objectType = property.getObjectType();
        description = property.getDescription();
        type = property.getType();
        observable = property.isObservable();
        readOnly = property.isReadOnly();
        writeOnly = property.isWriteOnly();
        forms = normalizeHrefs(property.getForms(), thing);
        uriVariables = property.getUriVariables();
        optionalProperties = property.getOptionalProperties();

        this.thing = thing;
    }

//    private List<Form> normalizeHrefs(List<Form> forms, ConsumedThing thing) {
//        return forms.stream().map(f -> normalizeHref(f, thing)).collect(Collectors.toList());
//    }
//
//    private Form normalizeHref(Form form, ConsumedThing thing) {
//        String base = thing.getBase();
//        if (base != null && !base.isEmpty() && !form.getHref().matches("^(?i:[a-z+]+:).*")) {
//            String normalizedHref = base + form.getHref();
//            return new Form.Builder(form).setHref(normalizedHref).build();
//        }
//        else {
//            return form;
//        }
//    }
//
    public CompletableFuture<T> read() {
        try {
            Pair<ProtocolClient, Form> clientAndForm = thing.getClientFor(getForms(), Operation.READ_PROPERTY);
            ProtocolClient client = clientAndForm.first();
            Form form = clientAndForm.second();

            log.debug("Thing '{}' reading Property '{}' from '{}'", thing.getId(), name, form.getHref());

            CompletableFuture<Content> result = client.readResource(form);
            return result.thenApply(content -> {
                try {
                    return ContentManager.contentToValue(content, this);
                }
                catch (ContentCodecException e) {
                    throw new CompletionException(new ConsumedThingException("Received invalid writeResource from Thing: " + e.getMessage()));
                }
            });
        }
        catch (ConsumedThingException e) {
            throw new CompletionException(e);
        }
    }

    public CompletableFuture<T> write(T value) {
        try {
            Pair<ProtocolClient, Form> clientAndForm = thing.getClientFor(getForms(), Operation.WRITE_PROPERTY);
            ProtocolClient client = clientAndForm.first();
            Form form = clientAndForm.second();

            log.debug("ConsumedThing {} writing {}", thing.getId(), form.getHref());

            Content input = ContentManager.valueToContent(value, form.getContentType());

            CompletableFuture<Content> result = client.writeResource(form, input);
            return result.thenApply(content -> {
                try {
                    return ContentManager.contentToValue(content, this);
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

    public Observable<Optional<T>> observer() throws ConsumedThingException {
        try {
            Pair<ProtocolClient, Form> clientAndForm = thing.getClientFor(getForms(), Operation.OBSERVE_PROPERTY);
            ProtocolClient client = clientAndForm.first();
            Form form = clientAndForm.second();

            return client.observeResource(form)
                    .map(content -> Optional.ofNullable(ContentManager.contentToValue(content, this)));
        }
        catch (ProtocolClientException e) {
            throw new ConsumedThingException(e);
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
        ConsumedThingProperty<?> that = (ConsumedThingProperty<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(thing, that.thing);
    }

    @Override
    public String toString() {
        return "ConsumedThingProperty{" +
                "name='" + name + '\'' +
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
