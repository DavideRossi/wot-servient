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

import city.sane.wot.binding.ProtocolClientFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Creates new {@link WebsocketProtocolClient} instances.
 */
public class WebsocketProtocolClientFactory implements ProtocolClientFactory {
    private final Set<WebsocketProtocolClient> clients = new HashSet<>();

    @Override
    public String getScheme() {
        return "ws";
    }

    @Override
    public WebsocketProtocolClient getClient() {
        WebsocketProtocolClient client = new WebsocketProtocolClient();
        clients.add(client);
        return client;
    }

    @Override
    public CompletableFuture<Void> destroy() {
        clients.forEach(WebsocketProtocolClient::destroy);
        return completedFuture(null);
    }
}
