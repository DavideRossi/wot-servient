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

import city.sane.wot.content.Content;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract class for all messages sent from {@link city.sane.wot.binding.websocket.WebsocketProtocolServer}
 * to {@link city.sane.wot.binding.websocket.WebsocketProtocolClient}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReadPropertyResponse.class, name = "ReadPropertyResponse"),
        @JsonSubTypes.Type(value = WritePropertyResponse.class, name = "WritePropertyResponse"),
        @JsonSubTypes.Type(value = InvokeActionResponse.class, name = "InvokeActionResponse"),
        @JsonSubTypes.Type(value = SubscribeNextResponse.class, name = "SubscribeNextResponse"),
        @JsonSubTypes.Type(value = SubscribeErrorResponse.class, name = "SubscribeErrorResponse"),
        @JsonSubTypes.Type(value = SubscribeCompleteResponse.class, name = "SubscribeCompleteResponse"),
        @JsonSubTypes.Type(value = ServerErrorResponse.class, name = "ServerErrorResponse"),
        @JsonSubTypes.Type(value = ClientErrorResponse.class, name = "ClientErrorResponse")
})
public abstract class AbstractServerMessage {
    protected final String id;

    public AbstractServerMessage(AbstractClientMessage message) {
        this(message.getId());
    }

    public AbstractServerMessage(String id) {
        this.id = id;
    }

    protected AbstractServerMessage() {
        id = null;
    }

    public String getId() {
        return id;
    }

    public abstract Content toContent();
}
