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
package city.sane.wot.binding.http;

import city.sane.wot.content.Content;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.security.BasicSecurityScheme;
import city.sane.wot.thing.security.BearerSecurityScheme;
import city.sane.wot.thing.security.SecurityScheme;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.observers.LambdaObserver;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpProtocolClientTest {
    private HttpClient requestClient;
    private Form form;
    private CloseableHttpResponse httpResponse;
    private StatusLine statusLine;
    private HttpEntity httpEntity;
    private HttpProtocolClient client;
    private SecurityScheme securityScheme;
    private Function<RequestConfig, CloseableHttpClient> clientCreator;
    private CloseableHttpClient httpClient;

    @BeforeEach
    public void setUp() {
        requestClient = mock(HttpClient.class);
        form = mock(Form.class);
        httpResponse = mock(CloseableHttpResponse.class);
        statusLine = mock(StatusLine.class);
        httpEntity = mock(HttpEntity.class);
        securityScheme = mock(SecurityScheme.class);
        clientCreator = mock(Function.class);
        httpClient = mock(CloseableHttpClient.class);
    }

    @Test
    public void readResourceShouldCreateProperRequest() throws IOException {
        when(form.getHref()).thenReturn("http://localhost/foo");
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(requestClient.execute(any())).thenReturn(httpResponse);

        client = new HttpProtocolClient(requestClient, clientCreator);
        client.readResource(form).join();

        HttpUriRequest request = RequestBuilder.create("GET").setUri("http://localhost/foo").build();
        verify(requestClient).execute(argThat(new HttpUriRequestMatcher(request)));
    }

    @Test
    public void readResourceShouldCreateProperRequestWithBasicAuth() throws IOException {
        when(form.getHref()).thenReturn("http://localhost/foo");
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(requestClient.execute(any())).thenReturn(httpResponse);

        Map credentials = Map.of(
                "username", "foo",
                "password", "bar"
        );
        client = new HttpProtocolClient(requestClient, clientCreator);
        client.setSecurity(List.of(new BasicSecurityScheme()), credentials);
        client.readResource(form).join();

        HttpUriRequest request = RequestBuilder.create("GET")
                .setUri("http://localhost/foo")
                .addHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encodeBase64(("foo:bar").getBytes())))
                .build();
        verify(requestClient).execute(argThat(new HttpUriRequestMatcher(request)));
    }

    @Test
    public void readResourceShouldCreateProperRequestWithBearerToken() throws IOException {
        when(form.getHref()).thenReturn("http://localhost/foo");
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(requestClient.execute(any())).thenReturn(httpResponse);

        Map credentials = Map.of(
                "token", "achu6Ahx"
        );
        client = new HttpProtocolClient(requestClient, clientCreator);
        client.setSecurity(List.of(new BearerSecurityScheme()), credentials);
        client.readResource(form).join();

        HttpUriRequest request = RequestBuilder.create("GET")
                .setUri("http://localhost/foo")
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer achu6Ahx")
                .build();
        verify(requestClient).execute(argThat(new HttpUriRequestMatcher(request)));
    }

    @Test
    public void subscribeResourceShouldCreateHttpRequest() throws IOException {
        when(clientCreator.apply(any())).thenReturn(httpClient);
        LambdaObserver<Content> observer = new LambdaObserver<>(n -> {
        }, e -> {
        }, () -> {
        }, s -> {
        });

        client = new HttpProtocolClient(requestClient, clientCreator);
        client.observeResource(form).subscribe(observer);

        verify(httpClient, timeout(1 * 1000L)).execute(any());
    }

    @Test
    public void observeResourceShouldCloseHttpRequestWhenObserverIsDone() throws IOException {
        when(clientCreator.apply(any())).thenReturn(httpClient);
        doAnswer(new AnswersWithDelay(10 * 1000L, invocation -> httpResponse)).when(httpClient).execute(any());

        client = new HttpProtocolClient(requestClient, clientCreator);
        Disposable subscribe = client.observeResource(form).subscribe();

        // wait until subscriptions as been established
        verify(httpClient, timeout(1 * 1000L)).execute(any());

        subscribe.dispose();

        verify(httpClient, timeout(5 * 1000L)).close();
    }

    private class HttpUriRequestMatcher implements ArgumentMatcher<HttpUriRequest> {
        private final HttpUriRequest left;

        public HttpUriRequestMatcher(HttpUriRequest left) {
            this.left = left;
        }

        @Override
        public boolean matches(HttpUriRequest right) {
            if (left == right) {
                return true;
            }
            if (right == null) {
                return false;
            }
            else {
                return left.getMethod().equals(right.getMethod()) &&
                        left.getURI().equals(right.getURI()) &&
                        Arrays.equals(left.getAllHeaders(), right.getAllHeaders(), (a, b) -> new HttpHeaderMatcher(a).matches(b) ? 0 : -1);
            }
        }
    }

    private class HttpHeaderMatcher implements ArgumentMatcher<Header> {
        private final Header left;

        public HttpHeaderMatcher(Header left) {
            this.left = left;
        }

        @Override
        public boolean matches(Header right) {
            if (left == right) {
                return true;
            }
            if (right == null) {
                return false;
            }
            else {
                if (left instanceof BasicHeader && right instanceof BasicHeader) {
                    BasicHeader basicLeft = (BasicHeader) left;
                    BasicHeader basicRight = (BasicHeader) right;

                    return basicLeft.getName().equals(basicRight.getName()) &&
                            basicLeft.getValue().equals(basicRight.getValue());
                }
                else {
                    throw new RuntimeException("HttpHeaderMatcher does not support this header type");
                }
            }
        }
    }
}