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

import city.sane.Pair;
import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ConsumedThing;
import city.sane.wot.thing.ConsumedThingException;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import io.reactivex.rxjava3.core.Observable;

import java.util.Objects;
import java.util.Optional;

/**
 * Used in combination with {@link ConsumedThing} and allows consuming of a {@link ThingEvent}.
 */
public class ConsumedThingEvent<T> extends ThingEvent<T> {
    private final String name;
    private final ConsumedThing thing;

    public ConsumedThingEvent(String name, ThingEvent<T> event, ConsumedThing thing) {
        this.name = name;
        forms = event.getForms();
        type = event.getType();
        data = event.getData();
        this.thing = thing;
    }

    public Observable<Optional<T>> observer() throws ConsumedThingException {
        try {
            Pair<ProtocolClient, Form> clientAndForm = thing.getClientFor(getForms(), Operation.SUBSCRIBE_EVENT);

            ProtocolClient client = clientAndForm.first();
            Form form = clientAndForm.second();

            return client.observeResource(form)
                    .map(content -> Optional.ofNullable(ContentManager.contentToValue(content, getData())));
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
        ConsumedThingEvent<?> that = (ConsumedThingEvent<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(thing, that.thing);
    }

    @Override
    public String toString() {
        return "ConsumedThingEvent{" +
                "name='" + name + '\'' +
                ", data=" + data +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", descriptions=" + descriptions +
                ", forms=" + forms +
                ", uriVariables=" + uriVariables +
                '}';
    }
}
