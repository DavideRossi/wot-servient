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

import city.sane.wot.content.Content;
import city.sane.wot.thing.Thing;
import city.sane.wot.thing.filter.ThingFilter;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.security.SecurityScheme;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.failedFuture;

/**
 * A ProtocolClient defines how to interact with a Thing via a specific protocol (e.g. HTTP, MQTT,
 * etc.).
 */
public interface ProtocolClient {
    /**
     * Reads the resource defined in <code>form</code>. This can be a {@link
     * city.sane.wot.thing.property.ThingProperty}, a {@link Thing} or a Thing Directory.
     *
     * @param form
     * @return
     */
    default CompletableFuture<Content> readResource(Form form) {
        return failedFuture(new ProtocolClientNotImplementedException(getClass(), "read"));
    }

    /**
     * Writes <code>content</code> to the resource defined in <code>form</code>. This can be, for
     * example, a {@link city.sane.wot.thing.property.ThingProperty}.
     *
     * @param form
     * @param content
     * @return
     */
    default CompletableFuture<Content> writeResource(Form form, Content content) {
        return failedFuture(new ProtocolClientNotImplementedException(getClass(), "write"));
    }

    /**
     * Invokes the resource defined in the <code>form</code>. This can be a {@link
     * city.sane.wot.thing.action.ThingAction}, for example.
     *
     * @param form
     * @return
     */
    default CompletableFuture<Content> invokeResource(Form form) {
        return invokeResource(form, null);
    }

    /**
     * Invokes the resource defined in the <code>form</code> with the payload defined in
     * <code>content</code>. This can be a {@link city.sane.wot.thing.action.ThingAction}, for
     * example.
     *
     * @param form
     * @param content
     * @return
     */
    default CompletableFuture<Content> invokeResource(Form form, Content content) {
        return failedFuture(new ProtocolClientNotImplementedException(getClass(), "invoke"));
    }

    /**
     * Create an observable for the resource defined in <code>form</code>. This resource can be, for
     * example, an {@link city.sane.wot.thing.event.ThingEvent} or an observable {@link
     * city.sane.wot.thing.property.ThingProperty}.
     *
     * @param form
     * @return
     */
    default Observable<Content> observeResource(Form form) throws ProtocolClientException {
        throw new ProtocolClientNotImplementedException(getClass(), "observe");
    }

    /**
     * Adds the <code>metadata</code> with security mechanisms (e.g. use password authentication)
     * and <code>credentials</code>credentials (e.g. password and username) of a things to the
     * client.
     *
     * @param metadata
     * @param credentials
     * @return
     */
    default boolean setSecurity(List<SecurityScheme> metadata, Object credentials) {
        return false;
    }

    /**
     * Starts the discovery process that will provide Things that match the <code>filter</code>
     * argument.
     *
     * @param filter
     * @return
     */
    default Observable<Thing> discover(ThingFilter filter) throws ProtocolClientNotImplementedException {
        throw new ProtocolClientNotImplementedException(getClass(), "discover");
    }
}
