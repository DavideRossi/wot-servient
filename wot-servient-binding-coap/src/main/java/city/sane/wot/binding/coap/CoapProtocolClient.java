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

import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.schema.StringSchema;
import io.reactivex.rxjava3.core.Observable;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Allows consuming Things via CoAP.
 */
@SuppressWarnings("squid:S1192")
public class CoapProtocolClient implements ProtocolClient {
    private static final Logger log = LoggerFactory.getLogger(CoapProtocolClient.class);
    private final Function<String, CoapClient> clientCreator;

    public CoapProtocolClient(ExecutorService executor) {
        this.clientCreator = url -> new CoapClient(url)
                .setExecutor(executor)
                .setTimeout(10 * 1000L);
    }

    CoapProtocolClient(Function<String, CoapClient> clientCreator) {
        this.clientCreator = clientCreator;
    }

    @Override
    public CompletableFuture<Content> readResource(Form form) {
        CompletableFuture<Content> future = new CompletableFuture<>();

        String url = form.getHref();
        CoapClient client = clientCreator.apply(url);

        Request request = generateRequest(form, CoAP.Code.GET);
        log.debug("CoapClient sending '{}' to '{}'", request.getCode(), url);

        client.advanced(new FutureCoapHandler(future), request);

        return future;
    }

    @Override
    public CompletableFuture<Content> writeResource(Form form, Content content) {
        CompletableFuture<Content> future = new CompletableFuture<>();

        String url = form.getHref();
        CoapClient client = clientCreator.apply(url);

        Request request = generateRequest(form, CoAP.Code.PUT);
        log.debug("CoapClient sending '{}' to '{}'", request.getCode(), url);
        if (content != null) {
            request.setPayload(content.getBody());
        }

        client.advanced(new FutureCoapHandler(future), request);

        return future;
    }

    @Override
    public CompletableFuture<Content> invokeResource(Form form, Content content) {
        CompletableFuture<Content> future = new CompletableFuture<>();

        String url = form.getHref();
        CoapClient client = clientCreator.apply(url);

        Request request = generateRequest(form, CoAP.Code.POST);
        log.debug("CoapClient sending '{}' to '{}'", request.getCode(), url);
        if (content != null) {
            request.setPayload(content.getBody());
        }

        client.advanced(new FutureCoapHandler(future), request);

        return future;
    }

    @Override
    public Observable<Content> observeResource(Form form) {
        String url = form.getHref();

        return Observable.using(
                () -> clientCreator.apply(url),
                client -> Observable.create(source -> {
                    // Californium does not offer any method to wait until the observation is established...
                    // This causes new values not being recognized directly after observation creation.
                    // The client must wait "some" time before it can be sure that the observation is active.
                    log.debug("CoapClient subscribe to '{}'", url);
                    client.observe(new CoapHandler() {
                        @Override
                        public void onLoad(CoapResponse response) {
                            String type = MediaTypeRegistry.toString(response.getOptions().getContentFormat());
                            byte[] body = response.getPayload();
                            Content output = new Content(type, body);
                            if (response.isSuccess()) {
                                log.debug("Next data received for subscription '{}'", url);
                                source.onNext(output);
                            }
                            else {
                                try {
                                    String error = ContentManager.contentToValue(output, new StringSchema());
                                    source.onError(new ProtocolClientException(error));
                                    log.debug("Error received for subscription '{}': {}", url, error);
                                }
                                catch (ContentCodecException e) {
                                    source.onError(new ProtocolClientException(e));
                                    log.debug("Error received for subscription '{}': {}", url, e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            source.onError(new ProtocolClientException("Error received for subscription '" + url + "'"));
                            log.debug("Error received for subscription '{}'", url);
                        }
                    });
                }),
                CoapClient::shutdown
        );
    }

    private Request generateRequest(Form form, CoAP.Code code) {
        return generateRequest(form, code, false);
    }

    private Request generateRequest(Form form, CoAP.Code code, boolean observable) {
        Request request = new Request(code);

        if (form.getContentType() != null) {
            int mediaType = MediaTypeRegistry.parse(form.getContentType());
            if (mediaType != -1) {
                request.getOptions().setContentFormat(mediaType);
            }
        }

        if (observable) {
            request.setObserve();
        }

        return request;
    }

    class FutureCoapHandler implements CoapHandler {
        private final CompletableFuture future;

        FutureCoapHandler(CompletableFuture future) {
            this.future = future;
        }

        @Override
        public void onLoad(CoapResponse response) {
            log.debug("Response received: {}", response.getCode());
            String type = MediaTypeRegistry.toString(response.getOptions().getContentFormat());
            byte[] body = response.getPayload();
            Content output = new Content(type, body);
            if (response.isSuccess()) {
                future.complete(output);
            }
            else {
                try {
                    String error = ContentManager.contentToValue(output, new StringSchema());
                    future.completeExceptionally(new ProtocolClientException("Request was not successful: " + response + " (" + error + ")"));
                }
                catch (ContentCodecException e) {
                    future.completeExceptionally(new ProtocolClientException("Request was not successful: " + response + " (" + e.getMessage() + ")"));
                }
            }
        }

        @Override
        public void onError() {
            future.completeExceptionally(new ProtocolClientException("request timeouts or has been rejected by the server"));
        }
    }
}
