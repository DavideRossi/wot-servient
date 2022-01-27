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
package city.sane.wot.binding.jadex;

import city.sane.RefCountResource;
import city.sane.RefCountResourceException;
import city.sane.wot.binding.ProtocolClientFactory;
import com.typesafe.config.Config;
import jadex.bridge.IExternalAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Creates new {@link JadexProtocolClient} instances. A Jadex Platform is created for this
 * purpose.<br> The Jadex Platform can be configured via the configuration parameter
 * "wot.servient.jadex.client".
 */
public class JadexProtocolClientFactory implements ProtocolClientFactory {
    private static final Logger log = LoggerFactory.getLogger(JadexProtocolClientFactory.class);
    private final RefCountResource<IExternalAccess> platformProvider;
    private IExternalAccess platform = null;

    public JadexProtocolClientFactory(Config wotConfig) {
        this(SharedPlatformProvider.singleton(wotConfig));
    }

    public JadexProtocolClientFactory(RefCountResource<IExternalAccess> platformProvider) {
        this.platformProvider = platformProvider;
    }

    @Override
    public String toString() {
        return "JadexClient";
    }

    @Override
    public String getScheme() {
        return "jadex";
    }

    @Override
    public JadexProtocolClient getClient() {
        return new JadexProtocolClient(platform);
    }

    @Override
    public CompletableFuture<Void> init() {
        log.debug("Create Jadex Platform");
        return runAsync(() -> {
            try {
                platform = platformProvider.retain();
            }
            catch (RefCountResourceException e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> destroy() {
        log.debug("Kill Jadex Platform");

        if (platform != null) {
            return runAsync(() -> {
                try {
                    platformProvider.release();
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

    public IExternalAccess getJadexPlatform() {
        return platform;
    }
}
