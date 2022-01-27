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
package city.sane.wot.binding.coap.resource;

import city.sane.Pair;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import io.reactivex.rxjava3.core.Observable;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

abstract class AbstractSubscriptionResource extends AbstractResource {
    private static final Logger log = LoggerFactory.getLogger(AbstractSubscriptionResource.class);
    private final String name;
    private final Observable<Optional<Object>> observable;
    private Pair<Optional, Throwable> last;

    AbstractSubscriptionResource(String resourceName,
                                 String name,
                                 Observable<Optional<Object>> observable) {
        super(resourceName);

        this.name = name;
        this.observable = observable;

        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs
        getAttributes().setObservable(); // mark observable in the Link-Format

        observable.subscribe(
                optional -> {
                    last = new Pair<>(optional, null);
                    changed();
                },
                e -> {
                    last = new Pair<>(null, e);
                    changed();
                }
        );
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        log.debug("Handle GET to '{}'", getURI());

        if (!exchange.advanced().getRequest().isAcknowledged()) {
            // The requestor should only be informed about new values.
            // send acknowledgement
            exchange.accept();

            ObserveRelation relation = exchange.advanced().getRelation();
            relation.setEstablished(true);
            addObserveRelation(relation);
        }
        else {
            Optional optional = last.first();
            Throwable e = last.second();

            String requestContentFormat = getOrDefaultRequestContentType(exchange);
            String subscribableType = observable.getClass().getSimpleName();
            if (e == null) {
                try {
                    Object data = optional.orElse(null);
                    log.debug("New data received for {} '{}': {}", subscribableType, name, data);
                    Content content = ContentManager.valueToContent(data, requestContentFormat);

                    int contentFormat = MediaTypeRegistry.parse(content.getType());
                    byte[] body = content.getBody();
                    if (body.length > 0) {
                        exchange.respond(CoAP.ResponseCode.CONTENT, body, contentFormat);
                    }
                    else {
                        exchange.respond(CoAP.ResponseCode.CONTENT);
                    }
                }
                catch (ContentCodecException ex) {
                    log.warn("Cannot process data for {} '{}': {}", subscribableType, name, ex.getMessage());
                    exchange.respond(CoAP.ResponseCode.SERVICE_UNAVAILABLE, "Invalid " + subscribableType + " Data");
                }
            }
            else {
                log.warn("Cannot process data for {} '{}': {}", subscribableType, name, e.getMessage());
                exchange.respond(CoAP.ResponseCode.SERVICE_UNAVAILABLE, "Invalid " + subscribableType + " Data");
            }
        }
    }
}
