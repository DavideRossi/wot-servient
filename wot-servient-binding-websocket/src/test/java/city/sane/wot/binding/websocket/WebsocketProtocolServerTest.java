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

import city.sane.wot.Servient;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.action.ExposedThingAction;
import city.sane.wot.thing.event.ExposedThingEvent;
import city.sane.wot.thing.property.ExposedThingProperty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebsocketProtocolServerTest {
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup serverBossGroup;
    private EventLoopGroup serverWorkerGroup;
    private Map<String, ExposedThing> things;
    private List<String> addresses;
    private int bindPort;
    private Channel serverChannel;
    private Servient servient;
    private ExposedThing thing;
    private ExposedThingProperty<Object> property;
    private ExposedThingEvent<Object> event;
    private ExposedThingAction<Object, Object> action;

    @BeforeEach
    public void setUp() {
        serverBootstrap = mock(ServerBootstrap.class);
        serverBossGroup = mock(EventLoopGroup.class);
        serverWorkerGroup = mock(EventLoopGroup.class);
        things = mock(Map.class);
        addresses = mock(List.class);
        bindPort = 80;
        serverChannel = mock(Channel.class);
        servient = mock(Servient.class);
        thing = mock(ExposedThing.class);
        property = mock(ExposedThingProperty.class);
        event = mock(ExposedThingEvent.class);
        action = mock(ExposedThingAction.class);
    }

    @Test
    public void startShouldBindToCorrectPort() {
        WebsocketProtocolServer server = new WebsocketProtocolServer(serverBootstrap, serverBossGroup, serverWorkerGroup, things, addresses, bindPort, null);
        server.start(servient);

        verify(serverBootstrap, timeout(1 * 1000L)).bind(80);
    }

    @Test
    public void stopShouldCloseServer() {
        WebsocketProtocolServer server = new WebsocketProtocolServer(serverBootstrap, serverBossGroup, serverWorkerGroup, things, addresses, bindPort, serverChannel);
        server.stop();

        verify(serverChannel, timeout(1 * 1000L)).close();
    }

    @Test
    public void exposeShouldAddFormsToThing() {
        when(thing.getId()).thenReturn("counter");
        when(thing.getProperties()).thenReturn(Map.of("count", property));
        when(property.isObservable()).thenReturn(true);
        when(thing.getActions()).thenReturn(Map.of("reset", action));
        when(thing.getEvents()).thenReturn(Map.of("changed", event));

        WebsocketProtocolServer server = new WebsocketProtocolServer(serverBootstrap, serverBossGroup, serverWorkerGroup, things, List.of("ws://localhost"), bindPort, serverChannel);
        server.expose(thing);

        verify(property, timeout(1 * 1000L).times(3)).addForm(any());
        verify(action, timeout(1 * 1000L)).addForm(any());
        verify(event, timeout(1 * 1000L)).addForm(any());
    }

    @Test
    public void destroyShouldRemoveThing() {
        when(thing.getId()).thenReturn("counter");

        WebsocketProtocolServer server = new WebsocketProtocolServer(serverBootstrap, serverBossGroup, serverWorkerGroup, things, List.of("ws://localhost"), bindPort, serverChannel);
        server.destroy(thing);

        verify(things, timeout(1 * 1000L)).remove("counter");
    }
}