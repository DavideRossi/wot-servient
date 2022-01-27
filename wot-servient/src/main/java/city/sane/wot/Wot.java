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
package city.sane.wot;

import city.sane.wot.thing.ConsumedThing;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.Thing;
import city.sane.wot.thing.filter.ThingFilter;
import io.reactivex.rxjava3.core.Observable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

/**
 * Provides methods for discovering, consuming, exposing and fetching things.
 * https://w3c.github.io/wot-scripting-api/#the-wot-api-object
 */
public interface Wot {
    /**
     * Starts the discovery process that will provide Things that match the <code>filter</code>
     * argument.
     *
     * @param filter
     * @return
     */
    Observable<Thing> discover(ThingFilter filter) throws WotException;

    /**
     * Starts the discovery process that will provide all available Things.
     *
     * @return
     */
    Observable<Thing> discover() throws WotException;

    /**
     * Accepts a <code>thing</code> argument of type {@link Thing} and returns an {@link
     * ExposedThing} object.<br> The result can be used to start exposing interfaces for thing
     * interaction. Returns a failed future if thing with same id is already exposed.
     *
     * @param thing
     * @return
     * @throws WotException If thing with same id is already exposed
     */
    ExposedThing produce(Thing thing) throws WotException;

    /**
     * Accepts a <code>thing</code> argument of type {@link Thing} and returns a {@link
     * ConsumedThing} object.<br> The result can be used to interact with a thing.
     *
     * @param thing
     * @return
     */
    ConsumedThing consume(Thing thing);

    /**
     * Accepts the <a href="https://www.w3.org/TR/wot-thing-description/">Thing Description</a> in
     * <code>thing</code> and returns a {@link ConsumedThing} object.<br> The result can be used to
     * interact with a thing.
     *
     * @param thing
     * @return
     */
    ConsumedThing consume(String thing);

    /**
     * Accepts an {@link java.net.URL} (e.g. "file:..." or "http://...") to a resource that serves a
     * thing description and returns the corresponding Thing object.
     *
     * @param url
     * @return
     */
    CompletableFuture<Thing> fetch(URI url);

    /**
     * Accepts an {@link String} containing an url (e.g. "file:..." or "http://...") to a resource
     * that serves a thing description and returns the corresponding Thing object.
     *
     * @param url
     * @return
     */
    CompletableFuture<Thing> fetch(String url) throws URISyntaxException;

    /**
     * Shut down the serving and stop exposing all things
     *
     * @return
     */
    CompletableFuture<Void> destroy();
}
