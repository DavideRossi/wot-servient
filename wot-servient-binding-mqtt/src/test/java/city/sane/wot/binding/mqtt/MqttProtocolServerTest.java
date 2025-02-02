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
package city.sane.wot.binding.mqtt;

import city.sane.Pair;
import city.sane.RefCountResource;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.action.ExposedThingAction;
import city.sane.wot.thing.event.ExposedThingEvent;
import city.sane.wot.thing.property.ExposedThingProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MqttProtocolServerTest {
    private MqttProtocolServer server;
    private RefCountResource refCountResource;
    private MqttProtocolSettings settings;
    private MqttClient mqttClient;
    private Pair<MqttProtocolSettings, MqttClient> settingsClientPair;
    private Multimap<String, Disposable> subcriptions;
    private ExposedThing thing;
    private ExposedThingProperty<Object> property;
    private ExposedThingAction action;
    private ExposedThingEvent event;

    @BeforeEach
    public void setUp() {
        settings = mock(MqttProtocolSettings.class);
        mqttClient = mock(MqttClient.class);
        refCountResource = mock(RefCountResource.class);
        settingsClientPair = mock(Pair.class);
        subcriptions = HashMultimap.create();
        thing = mock(ExposedThing.class);
        property = mock(ExposedThingProperty.class);
        action = mock(ExposedThingAction.class);
        event = mock(ExposedThingEvent.class);
    }

    @Test
    public void exposeShouldPublishThingDescription() throws MqttException {
        when(thing.getId()).thenReturn("counter");
        when(settingsClientPair.first()).thenReturn(settings);
        when(settingsClientPair.second()).thenReturn(mqttClient);
        when(settings.getBroker()).thenReturn("tcp://dummy-broker");
        server = new MqttProtocolServer(refCountResource, subcriptions, settingsClientPair);
        server.expose(thing);

        verify(mqttClient).publish(eq("counter"), any());
    }

    @Test
    public void exposeShouldExposeProperties() {
        when(thing.getId()).thenReturn("counter");
        when(thing.getProperties()).thenReturn(Map.of("count", property));
        when(property.observer()).thenReturn(PublishSubject.create());
        when(settingsClientPair.first()).thenReturn(settings);
        when(settingsClientPair.second()).thenReturn(mqttClient);
        when(settings.getBroker()).thenReturn("tcp://dummy-broker");

        server = new MqttProtocolServer(refCountResource, subcriptions, settingsClientPair);
        server.expose(thing);

        verify(property).addForm(any());
    }

    @Test
    public void exposeShouldExposeActions() {
        when(thing.getId()).thenReturn("counter");
        when(thing.getActions()).thenReturn(Map.of("increment", action));
        when(settingsClientPair.first()).thenReturn(settings);
        when(settingsClientPair.second()).thenReturn(mqttClient);
        when(settings.getBroker()).thenReturn("tcp://dummy-broker");

        server = new MqttProtocolServer(refCountResource, subcriptions, settingsClientPair);
        server.expose(thing);

        verify(action).addForm(any());
    }

    @Test
    public void exposeShouldExposeEvents() {
        when(thing.getId()).thenReturn("counter");
        when(thing.getEvents()).thenReturn(Map.of("changed", event));
        when(event.observer()).thenReturn(PublishSubject.create());
        when(settingsClientPair.first()).thenReturn(settings);
        when(settingsClientPair.second()).thenReturn(mqttClient);
        when(settings.getBroker()).thenReturn("tcp://dummy-broker");

        server = new MqttProtocolServer(refCountResource, subcriptions, settingsClientPair);
        server.expose(thing);

        verify(event).addForm(any());
    }
}
