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

import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * A ProtocolServer defines how to expose Thing for interaction via a specific protocol (e.g. HTTP,
 * MQTT, etc.).
 */
public interface ProtocolServer {
    /**
     * Starts the server (e.g. HTTP server) and makes it ready for requests to the exposed things.
     *
     * @param servient
     * @return
     */
    CompletableFuture<Void> start(Servient servient);

    /**
     * Stops the server (e.g. HTTP server) and ends the exposure of the Things
     *
     * @return
     */
    CompletableFuture<Void> stop();

    /**
     * Exposes <code>thing</code> and allows interaction with it.
     *
     * @param thing
     * @return
     */
    CompletableFuture<Void> expose(ExposedThing thing);

    /**
     * Stops the exposure of <code>thing</code> and allows no further interaction with the thing.
     *
     * @param thing
     * @return
     */
    CompletableFuture<Void> destroy(ExposedThing thing);

    /**
     * Returns the URL to the Thing Directory if the server supports the listing of all Thing
     * Descriptions.
     *
     * @return
     * @throws ProtocolServerException
     */
    default URI getDirectoryUrl() throws ProtocolServerException {
        throw new ProtocolServerNotImplementedException(getClass(), "directory");
    }

    /**
     * Returns the URL to the thing with the id <code>id</code> if the server supports the listing
     * of certain Thing Descriptions.
     *
     * @param id
     * @return
     * @throws ProtocolServerException
     */
    default URI getThingUrl(String id) throws ProtocolServerException {
        throw new ProtocolServerNotImplementedException(getClass(), "thing-url");
    }
}
