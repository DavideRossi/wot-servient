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
import com.typesafe.config.Config;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a Singleton class, which is used by {@link MqttProtocolClient} and {@link
 * MqttProtocolServer} to share a single MqttClient.
 */
public class SharedMqttClientProvider {
    private static final Map<Config, RefCountResource<Pair<MqttProtocolSettings, MqttClient>>> singletons = new HashMap<>();

    private SharedMqttClientProvider() {
        // singleton class
    }

    public static synchronized RefCountResource<Pair<MqttProtocolSettings, MqttClient>> singleton(
            Config config) {
        return singletons.computeIfAbsent(
                config,
                myConfig -> new RefCountResource<>(
                        () -> {
                            MqttProtocolSettings settings = new MqttProtocolSettings(myConfig);
                            settings.validate();
                            return new Pair<>(settings, settings.createConnectedMqttClient());
                        },
                        pair -> pair.second().disconnect()
                )
        );
    }
}
