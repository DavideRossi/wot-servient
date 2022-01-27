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
package city.sane.wot.binding.websocket.message;

import city.sane.wot.thing.ExposedThing;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Abstract class for all messages sent from {@link city.sane.wot.binding.websocket.WebsocketProtocolClient}
 * to {@link city.sane.wot.binding.websocket.WebsocketProtocolServer}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReadProperty.class, name = "ReadProperty"),
        @JsonSubTypes.Type(value = WriteProperty.class, name = "WriteProperty"),
        @JsonSubTypes.Type(value = InvokeAction.class, name = "InvokeAction"),
        @JsonSubTypes.Type(value = SubscribeProperty.class, name = "SubscribeProperty"),
        @JsonSubTypes.Type(value = SubscribeEvent.class, name = "SubscribeEvent")
})
public abstract class AbstractClientMessage {
    private final String id;

    protected AbstractClientMessage() {
        id = randomId();
    }

    /**
     * Generates a new random ID to make the message uniquely identifiable.
     *
     * @return
     */
    public static String randomId() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public String getId() {
        return id;
    }

    /**
     * Creates the server's response to the request sent by the client.
     *
     * @param replyConsumer
     * @param things
     * @return
     */
    public abstract void reply(Consumer<AbstractServerMessage> replyConsumer,
                               Map<String, ExposedThing> things);
}
