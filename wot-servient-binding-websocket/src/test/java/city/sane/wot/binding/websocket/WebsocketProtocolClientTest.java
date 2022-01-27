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
package city.sane.wot.binding.websocket;

import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.binding.websocket.WebsocketProtocolClient.WebsocketClient;
import city.sane.wot.binding.websocket.message.AbstractServerMessage;
import city.sane.wot.binding.websocket.message.InvokeAction;
import city.sane.wot.binding.websocket.message.ReadProperty;
import city.sane.wot.binding.websocket.message.SubscribeProperty;
import city.sane.wot.binding.websocket.message.WriteProperty;
import city.sane.wot.content.Content;
import city.sane.wot.thing.form.Form;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

import static city.sane.wot.binding.websocket.WebsocketProtocolServer.WEBSOCKET_MESSAGE_NAME;
import static city.sane.wot.binding.websocket.WebsocketProtocolServer.WEBSOCKET_MESSAGE_THING_ID;
import static city.sane.wot.binding.websocket.WebsocketProtocolServer.WEBSOCKET_MESSAGE_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebsocketProtocolClientTest {
    private Map<URI, WebsocketClient> clients;
    private Map<String, Consumer<AbstractServerMessage>> openRequests;
    private Form form;
    private WebsocketClient websocketClient;
    private Content content;

    @BeforeEach
    public void setUp() {
        clients = mock(Map.class);
        openRequests = mock(Map.class);
        form = mock(Form.class);
        websocketClient = mock(WebsocketClient.class);
        content = mock(Content.class);
    }

    @Test
    public void readResourceShouldSendMessageGivenInFormToSocket() {
        when(form.getOptional(any())).thenReturn(Map.of(
                WEBSOCKET_MESSAGE_TYPE, "ReadProperty",
                WEBSOCKET_MESSAGE_THING_ID, "counter",
                WEBSOCKET_MESSAGE_NAME, "count"
        ));
        when(form.getHref()).thenReturn("wss://localhost");
        when(clients.get(any())).thenReturn(websocketClient);
        when(websocketClient.isOpen()).thenReturn(true);

        WebsocketProtocolClient client = new WebsocketProtocolClient(clients, openRequests);
        client.readResource(form);

        verify(websocketClient, timeout(1 * 1000L)).send(any(ReadProperty.class));
    }

    @Test
    public void writeResourceShouldSendMessageGivenInFormToSocket() {
        when(form.getOptional(any())).thenReturn(Map.of(
                WEBSOCKET_MESSAGE_TYPE, "WriteProperty",
                WEBSOCKET_MESSAGE_THING_ID, "counter",
                WEBSOCKET_MESSAGE_NAME, "count"
        ));
        when(form.getHref()).thenReturn("wss://localhost");
        when(clients.get(any())).thenReturn(websocketClient);
        when(websocketClient.isOpen()).thenReturn(true);

        WebsocketProtocolClient client = new WebsocketProtocolClient(clients, openRequests);
        client.writeResource(form, content);

        verify(websocketClient, timeout(1 * 1000L)).send(any(WriteProperty.class));
    }

    @Test
    public void invokeResourceShouldSendMessageGivenInFormToSocket() {
        when(form.getOptional(any())).thenReturn(Map.of(
                WEBSOCKET_MESSAGE_TYPE, "InvokeAction",
                WEBSOCKET_MESSAGE_THING_ID, "counter",
                WEBSOCKET_MESSAGE_NAME, "reset"
        ));
        when(form.getHref()).thenReturn("wss://localhost");
        when(clients.get(any())).thenReturn(websocketClient);
        when(websocketClient.isOpen()).thenReturn(true);

        WebsocketProtocolClient client = new WebsocketProtocolClient(clients, openRequests);
        client.invokeResource(form);

        verify(websocketClient, timeout(1 * 1000L)).send(any(InvokeAction.class));
    }

    @Test
    public void observeResourceShouldSendMessageGivenInFormToSocket() throws ProtocolClientException {
        when(form.getOptional(any())).thenReturn(Map.of(
                WEBSOCKET_MESSAGE_TYPE, "SubscribeProperty",
                WEBSOCKET_MESSAGE_THING_ID, "counter",
                WEBSOCKET_MESSAGE_NAME, "count"
        ));
        when(form.getHref()).thenReturn("wss://localhost");
        when(clients.get(any())).thenReturn(websocketClient);
        when(websocketClient.isOpen()).thenReturn(true);

        WebsocketProtocolClient client = new WebsocketProtocolClient(clients, openRequests);
        client.observeResource(form).subscribe();

        verify(websocketClient, timeout(1 * 1000L)).send(any(SubscribeProperty.class));
    }
}