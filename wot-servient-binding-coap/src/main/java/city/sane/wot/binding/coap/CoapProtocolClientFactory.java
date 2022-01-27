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

import city.sane.wot.binding.ProtocolClientFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Creates new {@link CoapProtocolClient} instances.
 */
public class CoapProtocolClientFactory implements ProtocolClientFactory {
    static {
        // Californium uses java.util.logging. We need to redirect all log messages to logback
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private final ExecutorService executor;

    public CoapProtocolClientFactory() {
        executor = Executors.newFixedThreadPool(10);
    }

    @Override
    public String toString() {
        return "CoapClient";
    }

    @Override
    public String getScheme() {
        return "coap";
    }

    @Override
    public CoapProtocolClient getClient() {
        return new CoapProtocolClient(executor);
    }
}
