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
package city.sane.wot.binding.coap;

import city.sane.wot.content.Content;
import city.sane.wot.thing.form.Form;
import org.eclipse.californium.core.CoapClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoapProtocolClientTest {
    private ExecutorService executor;
    private Function<String, CoapClient> clientCreator;
    private CoapClient coapClient;
    private Form form;
    private Content content;

    @BeforeEach
    public void setUp() throws Exception {
        executor = mock(ExecutorService.class);
        clientCreator = mock(Function.class);
        coapClient = mock(CoapClient.class);
        form = mock(Form.class);
        content = mock(Content.class);
    }

    @Test
    public void readResourceShouldCreateCoapRequest() {
        when(clientCreator.apply(any())).thenReturn(coapClient);

        CoapProtocolClient client = new CoapProtocolClient(clientCreator);
        client.readResource(form);

        verify(coapClient, timeout(1 * 1000L)).advanced(any(), any());
    }

    @Test
    public void writeResourceShouldCreateCoapRequest() {
        when(clientCreator.apply(any())).thenReturn(coapClient);

        CoapProtocolClient client = new CoapProtocolClient(clientCreator);
        client.writeResource(form, content);

        verify(coapClient, timeout(1 * 1000L)).advanced(any(), any());
    }

    @Test
    public void invokeResourceShouldCreateCoapRequest() {
        when(form.getHref()).thenReturn("coap://localhost/counter/actions/reset");
        when(clientCreator.apply(any())).thenReturn(coapClient);

        CoapProtocolClient client = new CoapProtocolClient(clientCreator);
        client.invokeResource(form);

        verify(coapClient, timeout(1 * 1000L)).advanced(any(), any());
    }

    @Test
    public void observeResourceShouldCreateCoapRequest() {
        when(form.getHref()).thenReturn("coap://localhost/counter/properties/count");
        when(clientCreator.apply(any())).thenReturn(coapClient);

        CoapProtocolClient client = new CoapProtocolClient(clientCreator);
        client.observeResource(form).subscribe();

        verify(coapClient, timeout(1 * 1000L)).observe(any());
    }
}