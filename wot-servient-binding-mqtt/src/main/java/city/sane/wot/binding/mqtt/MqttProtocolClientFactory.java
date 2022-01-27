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
import city.sane.RefCountResourceException;
import city.sane.wot.ServientDiscoveryIgnore;
import city.sane.wot.binding.ProtocolClientFactory;
import com.typesafe.config.Config;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Creates new {@link MqttProtocolClient} instances.
 */
@ServientDiscoveryIgnore
public class MqttProtocolClientFactory implements ProtocolClientFactory {
    private static final Logger log = LoggerFactory.getLogger(MqttProtocolClientFactory.class);
    private final RefCountResource<Pair<MqttProtocolSettings, MqttClient>> settingsClientPairProvider;
    private Pair<MqttProtocolSettings, MqttClient> settingsClientPair;

    public MqttProtocolClientFactory(Config config) {
        settingsClientPairProvider = SharedMqttClientProvider.singleton(config);
    }

    @Override
    public String getScheme() {
        return "mqtt";
    }

    @Override
    public MqttProtocolClient getClient() {
        return new MqttProtocolClient(settingsClientPair);
    }

    @Override
    public CompletableFuture<Void> init() {
        log.debug("Init MqttClient");

        if (settingsClientPair == null) {
            return runAsync(() -> {
                try {
                    settingsClientPair = settingsClientPairProvider.retain();
                }
                catch (RefCountResourceException e) {
                    throw new CompletionException(e);
                }
            });
        }
        else {
            return completedFuture(null);
        }
    }

    @Override
    public CompletableFuture<Void> destroy() {
        log.debug("Disconnect MqttClient");

        if (settingsClientPair != null) {
            return runAsync(() -> {
                try {
                    settingsClientPairProvider.release();
                }
                catch (RefCountResourceException e) {
                    throw new CompletionException(e);
                }
            });
        }
        else {
            return completedFuture(null);
        }
    }
}
