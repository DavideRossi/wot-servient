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
package city.sane.wot.thing;

import city.sane.Pair;
import city.sane.wot.Servient;
import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsumedThingTest {
    private Servient servient;
    private ProtocolClient client;
    private Form form1;
    private Form form2;
    private Operation op;
    private Thing thing;

    @BeforeEach
    public void setUp() {
        servient = mock(Servient.class);
        client = mock(ProtocolClient.class);
        thing = mock(Thing.class);
        form1 = mock(Form.class);
        form2 = mock(Form.class);
        op = Operation.READ_PROPERTY;
    }

    @Test
    public void getClientFor() throws ConsumedThingException, ProtocolClientException {
        when(servient.hasClientFor("test")).thenReturn(true);
        when(servient.getClientFor("test")).thenReturn(client);
        when(form1.getHrefScheme()).thenReturn("test");

        ConsumedThing consumedThing = new ConsumedThing(servient, thing);
        Pair<ProtocolClient, Form> clientFor = consumedThing.getClientFor(form1, op);

        assertEquals(client, clientFor.first());
        assertEquals(form1, clientFor.second());
    }

    // If the client support multiple protocols offered by the thing, it should select the protocol which is first in list
    @Test
    public void getClientForFirstInClient() throws ConsumedThingException, ProtocolClientException {
        when(servient.hasClientFor("test")).thenReturn(true);
        when(servient.getClientFor("test")).thenReturn(client);
        when(servient.getClientSchemes()).thenReturn(List.of("test", "bar"));
        when(form1.getHrefScheme()).thenReturn("bar");
        when(form2.getHrefScheme()).thenReturn("test");

        ConsumedThing consumedThing = new ConsumedThing(servient, thing);
        Pair<ProtocolClient, Form> clientFor = consumedThing.getClientFor(List.of(form1, form2), op);

        assertEquals(client, clientFor.first());
        assertEquals(form2, clientFor.second());
    }

    @Test
    public void getClientForWithUnsupportedOperation() throws ConsumedThingException, ProtocolClientException {
        when(servient.hasClientFor("test")).thenReturn(true);
        when(servient.getClientFor("test")).thenReturn(client);
        when(form1.getHrefScheme()).thenReturn("test");
        when(form1.getOp()).thenReturn(List.of(Operation.READ_PROPERTY));

        ConsumedThing consumedThing = new ConsumedThing(servient, thing);
        assertThrows(NoFormForInteractionConsumedThingException.class, () -> consumedThing.getClientFor(form1, Operation.WRITE_PROPERTY));
    }

    @Test
    public void getClientForWithUnsupportedProtocol() {
        when(form1.getHrefScheme()).thenReturn("http");

        ConsumedThing consumedThing = new ConsumedThing(servient, thing);
        assertThrows(NoClientFactoryForSchemesConsumedThingException.class, () -> consumedThing.getClientFor(form1, Operation.WRITE_PROPERTY));
    }

    @Test
    public void readAllProperties() throws ExecutionException, InterruptedException, ProtocolClientException {
        when(servient.hasClientFor("test")).thenReturn(true);
        when(servient.getClientFor("test")).thenReturn(client);
        when(form1.getHrefScheme()).thenReturn("test");
        when(thing.getForms()).thenReturn(List.of(form1));
        when(client.readResource(any())).thenReturn(completedFuture(null));

        ConsumedThing consumedThing = new ConsumedThing(servient, thing);
        consumedThing.readProperties().get();

        verify(client).readResource(form1);
    }

    @Test
    public void readSomeProperties() throws ExecutionException, InterruptedException, ProtocolClientException, ContentCodecException {
        when(servient.hasClientFor("test")).thenReturn(true);
        when(servient.getClientFor("test")).thenReturn(client);
        when(form1.getHrefScheme()).thenReturn("test");
        when(thing.getForms()).thenReturn(List.of(form1));
        when(client.readResource(any())).thenReturn(completedFuture(ContentManager.valueToContent(Map.of("foo", 1, "bar", 2, "baz", 3))));

        ConsumedThing consumedThing = new ConsumedThing(servient, thing);

        Map<String, Object> values = consumedThing.readProperties("foo", "bar").get();
        assertThat(values, hasEntry("foo", 1));
        assertThat(values, hasEntry("bar", 2));
        assertThat(values, not(hasEntry("baz", 3)));
        verify(client).readResource(form1);
    }

    @Test
    public void handleUriVariables() {
        Form form = new Form.Builder().setHref("http://192.168.178.24:8080/counter/actions/increment{?step}").build();
        Map<String, Object> parameters = Map.of("step", 3);

        Form result = ConsumedThing.handleUriVariables(form, parameters);

        assertEquals("http://192.168.178.24:8080/counter/actions/increment?step=3", result.getHref());
    }

    @Test
    public void handleUriVariablesMultiple() {
        Form form = new Form.Builder().setHref("http://192.168.178.24:8080/counter/actions/increment{?step,direction}").build();
        Map<String, Object> parameters = Map.of("step", 3, "direction", "up");

        Form result = ConsumedThing.handleUriVariables(form, parameters);

        assertEquals("http://192.168.178.24:8080/counter/actions/increment?step=3&direction=up", result.getHref());
    }

    @Test
    public void testEquals() {
        ConsumedThing thingA = new ConsumedThing(null, new Thing.Builder().setId("counter").build());
        ConsumedThing thingB = new ConsumedThing(null, new Thing.Builder().setId("counter").build());

        assertEquals(thingA, thingB);
    }
}