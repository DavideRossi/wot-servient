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
package city.sane.wot.binding;

import city.sane.wot.Servient;
import city.sane.wot.thing.ExposedThing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProtocolServerTest {
    private String id;

    @BeforeEach
    public void setUp() {
        id = "counter";
    }

    @Test
    public void getDirectoryUrl() throws ProtocolServerException {
        assertThrows(ProtocolServerNotImplementedException.class, () -> new MyProtocolServer().getDirectoryUrl());
    }

    @Test
    public void getThingUrl() throws ProtocolServerException {
        assertThrows(ProtocolServerNotImplementedException.class, () -> new MyProtocolServer().getThingUrl(id));
    }

    class MyProtocolServer implements ProtocolServer {
        /**
         * Starts the server (e.g. HTTP server) and makes it ready for requests to the exposed
         * things.
         *
         * @param servient
         * @return
         */
        @Override
        public CompletableFuture<Void> start(Servient servient) {
            return null;
        }

        /**
         * Stops the server (e.g. HTTP server) and ends the exposure of the Things
         *
         * @return
         */
        @Override
        public CompletableFuture<Void> stop() {
            return null;
        }

        /**
         * Exposes <code>thing</code> and allows interaction with it.
         *
         * @param thing
         * @return
         */
        @Override
        public CompletableFuture<Void> expose(ExposedThing thing) {
            return null;
        }

        /**
         * Stops the exposure of <code>thing</code> and allows no further interaction with the
         * thing.
         *
         * @param thing
         * @return
         */
        @Override
        public CompletableFuture<Void> destroy(ExposedThing thing) {
            return null;
        }
    }
}