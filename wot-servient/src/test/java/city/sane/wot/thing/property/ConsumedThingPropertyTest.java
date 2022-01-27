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
import city.sane.wot.thing.ConsumedThing;
import city.sane.wot.thing.ConsumedThingException;
import city.sane.wot.thing.form.Form;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsumedThingPropertyTest {
    private ThingProperty<Object> property;
    private ConsumedThing thing;
    private Form form;
    private ProtocolClient client;
    private Observer observer;
    private Observable<Content> observable;

    @BeforeEach
    public void setUp() {
        property = mock(ThingProperty.class);
        thing = mock(ConsumedThing.class);
        form = mock(Form.class);
        client = mock(ProtocolClient.class);
        observer = mock(Observer.class);
        observable = mock(Observable.class);
    }

    @Test
    public void normalizeAbsoluteHref() {
        when(property.getForms()).thenReturn(List.of(form));
        when(form.getHref()).thenReturn("http://example.com/properties/count");

        ConsumedThingProperty<Object> consumedThingProperty = new ConsumedThingProperty<Object>("myProperty", property, thing);

        assertEquals("http://example.com/properties/count", consumedThingProperty.getForms().get(0).getHref());
    }

    @Test
    public void normalizeRelativeHref() {
        when(thing.getBase()).thenReturn("http://example.com");
        when(property.getForms()).thenReturn(List.of(form));
        when(form.getHref()).thenReturn("/properties/count");

        ConsumedThingProperty<Object> consumedThingProperty = new ConsumedThingProperty<Object>("myProperty", property, thing);

        assertEquals("http://example.com/properties/count", consumedThingProperty.getForms().get(0).getHref());
    }

    @Test
    public void normalizeAbsoluteHrefWithBase() {
        when(thing.getBase()).thenReturn("http://example.com");
        when(property.getForms()).thenReturn(List.of(form));
        when(form.getHref()).thenReturn("http://example.com/properties/count");

        ConsumedThingProperty<Object> consumedThingProperty = new ConsumedThingProperty<Object>("myProperty", property, thing);

        assertEquals("http://example.com/properties/count", consumedThingProperty.getForms().get(0).getHref());
    }

    @Test
    public void readShouldCallUnderlyingClient() throws ConsumedThingException {
        when(thing.getClientFor(any(List.class), any())).thenReturn(new Pair(client, form));
        when(client.readResource(any())).thenReturn(completedFuture(null));

        ConsumedThingProperty<Object> consumedThingProperty = new ConsumedThingProperty<Object>("myProperty", property, thing);
        consumedThingProperty.read();

        verify(client).readResource(any());
    }

    @Test
    public void writeShouldCallUnderlyingClient() throws ConsumedThingException {
        when(thing.getClientFor(any(List.class), any())).thenReturn(new Pair(client, form));
        when(client.writeResource(any(), any())).thenReturn(completedFuture(null));

        ConsumedThingProperty<Object> consumedThingProperty = new ConsumedThingProperty<Object>("myProperty", property, thing);
        consumedThingProperty.write("123");

        verify(client).writeResource(any(), any());
    }

    @Test
    public void subscribeShoulCallUnderlyingClient() throws ConsumedThingException, ProtocolClientException {
        when(thing.getClientFor(any(List.class), any())).thenReturn(new Pair(client, form));
        when(client.observeResource(any())).thenReturn(observable);

        ConsumedThingProperty<Object> consumedThingProperty = new ConsumedThingProperty<Object>("myProperty", property, thing);
        consumedThingProperty.observer().subscribe(observer);

        verify(client).observeResource(any());
    }
}