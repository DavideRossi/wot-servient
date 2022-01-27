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
import city.sane.wot.thing.filter.ThingFilter;
import city.sane.wot.thing.form.Form;
import io.reactivex.rxjava3.core.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class ProtocolClientTest {
    private Form form;
    private Content content;
    private Observer observer;
    private List metadata;
    private Object credentials;
    private ThingFilter filter;
    private ProtocolClient client;

    @BeforeEach
    public void setUp() {
        form = mock(Form.class);
        content = mock(Content.class);
        observer = mock(Observer.class);
        metadata = mock(List.class);
        credentials = mock(Object.class);
        filter = mock(ThingFilter.class);
        client = spy(ProtocolClient.class);
    }

    @Test
    public void readResourceShouldThrowProtocolClientNotImplementedException() {
        assertThrows(ProtocolClientNotImplementedException.class, () -> {
            try {
                client.readResource(form).get();
            }
            catch (ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void writeResourceShouldThrowProtocolClientNotImplementedException() {
        assertThrows(ProtocolClientNotImplementedException.class, () -> {
            try {
                client.writeResource(form, content).get();
            }
            catch (ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void invokeResourceShouldThrowProtocolClientNotImplementedException() {
        assertThrows(ProtocolClientNotImplementedException.class, () -> {
            try {
                client.invokeResource(form).get();
            }
            catch (ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void subscribeResourceShouldThrowProtocolClientNotImplementedException() {
        assertThrows(ProtocolClientNotImplementedException.class, () -> client.observeResource(form).subscribe(observer));
    }

    @Test
    public void setSecurityShouldReturnFalse() {
        assertFalse(client.setSecurity(metadata, credentials));
    }

    @Test
    public void discoverShouldThrowProtocolClientNotImplementedException() {
        assertThrows(ProtocolClientNotImplementedException.class, () -> client.discover(filter));
    }
}
