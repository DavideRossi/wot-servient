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

import com.typesafe.config.Config;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MqttProtocolSettings {
    private static final Logger log = LoggerFactory.getLogger(MqttProtocolSettings.class);
    private final String broker;
    private final String clientId;
    private final String username;
    private final String password;

    public MqttProtocolSettings(Config config) {
        if (config.hasPath("wot.servient.mqtt.broker")) {
            broker = config.getString("wot.servient.mqtt.broker");
        }
        else {
            broker = null;
        }

        if (config.hasPath("wot.servient.mqtt.client-id")) {
            clientId = config.getString("wot.servient.mqtt.client-id");
        }
        else {
            // generate random client id
            clientId = "wot" + System.nanoTime();
        }

        if (config.hasPath("wot.servient.mqtt.username")) {
            username = config.getString("wot.servient.mqtt.username");
        }
        else {
            username = null;
        }

        if (config.hasPath("wot.servient.mqtt.password")) {
            password = config.getString("wot.servient.mqtt.password");
        }
        else {
            password = null;
        }
    }

    MqttProtocolSettings(String broker, String clientId, String username, String password) {
        this.broker = broker;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public void validate() throws MqttProtocolException {
        if (getBroker() == null || getBroker().isEmpty()) {
            throw new MqttProtocolException("No broker defined for MQTT server binding - skipping");
        }
    }

    public String getBroker() {
        return broker;
    }

    public MqttClient createConnectedMqttClient() throws MqttProtocolException {
        try (MqttClientPersistence persistence = new MemoryPersistence()) {
            MqttClient client = new MqttClient(getBroker(), getClientId(), persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            if (getUsername() != null) {
                options.setUserName(getUsername());
            }
            if (getPassword() != null) {
                options.setPassword(getPassword().toCharArray());
            }

            log.info("MqttClient trying to connect to broker at '{}' with client ID '{}'", getBroker(), getClientId());
            client.connect(options);
            log.info("MqttClient connected to broker at '{}'", getBroker());

            return client;
        }
        catch (MqttException e) {
            throw new MqttProtocolException(e);
        }
    }

    public String getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
