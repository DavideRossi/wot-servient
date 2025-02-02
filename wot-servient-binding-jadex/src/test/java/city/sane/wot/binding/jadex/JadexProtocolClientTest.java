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

import city.sane.wot.thing.Thing;
import city.sane.wot.thing.filter.ThingFilter;
import io.reactivex.rxjava3.annotations.NonNull;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JadexProtocolClientTest {
    private IExternalAccess platform;
    private JadexProtocolClient client;
    private ThingFilter filter;
    private IInternalAccess internalPlatform;
    private ITerminableIntermediateFuture<Object> searchFuture;
    private ThingService thingService;
    private IFuture<String> thingServiceFuture;

    @BeforeEach
    public void setUp() {
        platform = mock(IExternalAccess.class);
        filter = mock(ThingFilter.class);
        internalPlatform = mock(IInternalAccess.class);
        searchFuture = mock(ITerminableIntermediateFuture.class);
        thingService = mock(ThingService.class);
        thingServiceFuture = mock(IFuture.class);
    }

    @Test
    public void discoverShouldReturnFoundThings() {
        when(platform.scheduleStep(any())).then(invocationOnMock -> {
            IComponentStep step = invocationOnMock.getArgument(0, IComponentStep.class);
            step.execute(internalPlatform);
            return null;
        });
        when(internalPlatform.searchServices(any())).thenReturn(searchFuture);
        doAnswer(invocationOnMock -> {
            IntermediateDefaultResultListener resultListener = invocationOnMock.getArgument(0, IntermediateDefaultResultListener.class);
            resultListener.intermediateResultAvailable(thingService);
            resultListener.finished();
            return null;
        }).when(searchFuture).addResultListener(any());
        when(thingService.get()).thenReturn(thingServiceFuture);
        doAnswer(invocationOnMock -> {
            IResultListener resultListener = invocationOnMock.getArgument(0, IResultListener.class);
            resultListener.resultAvailable("{\"id\":\"counter\",\"title\":\"Zähler\"}");
            return null;
        }).when(thingServiceFuture).addResultListener(any());

        client = new JadexProtocolClient(platform);
        @NonNull List<Thing> things = client.discover(filter).toList().blockingGet();

        assertThat(things, hasItem(new Thing.Builder().setId("counter").setTitle("Zähler").build()));
    }
}