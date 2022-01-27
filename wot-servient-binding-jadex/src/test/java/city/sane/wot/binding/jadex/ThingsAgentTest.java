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

import city.sane.wot.thing.ExposedThing;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThingsAgentTest {
    private IInternalAccess ia;
    private ExposedThing thing;
    private Map<String, IExternalAccess> children;
    private Map<String, ExposedThing> things;
    private IExternalAccess externalAccess;

    @BeforeEach
    public void setup() {
        ia = mock(IInternalAccess.class);
        thing = mock(ExposedThing.class);
        things = mock(Map.class);
        children = mock(Map.class);
        externalAccess = mock(IExternalAccess.class);
    }

    @Test
    public void createdShouldNotFail() {
        ThingsAgent agent = new ThingsAgent(ia, Map.of("counter", thing), children);
        agent.created();

        // shot not fail
        assertTrue(true);
    }

    @Test
    public void exposeShouldCreateThingAgent() {
        when(ia.createComponent(any())).thenReturn(mock(IFuture.class));

        ThingsAgent agent = new ThingsAgent(ia, things, children);

        agent.expose("counter");

        // CreateionInfo ist not comparable
//        CreationInfo info = new CreationInfo()
//                .setFilenameClass(ThingAgent.class)
//                .addArgument("thing", thing);
        verify(ia).createComponent(any());
    }

    @Test
    public void destroyShouldDestroyThingAgent() {
        when(children.get(any())).thenReturn(externalAccess);
        when(externalAccess.killComponent()).thenReturn(mock(IFuture.class));

        ThingsAgent agent = new ThingsAgent(ia, things, children);
        agent.destroy("counter");

        verify(externalAccess).killComponent();
    }
}