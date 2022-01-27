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
package city.sane.wot.binding.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import city.sane.RefCountResource;
import city.sane.Triple;
import city.sane.wot.binding.akka.actor.ThingsActor;
import city.sane.wot.thing.ExposedThing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AkkaProtocolServerTest {
    private ExposedThing thing;
    private RefCountResource<Triple<ActorSystem, Map<String, ExposedThing>, ActorRef>> actorSystemProvider;
    private AkkaProtocolPattern pattern;
    private Triple<ActorSystem, Map<String, ExposedThing>, ActorRef> triple;
    private Map<String, ExposedThing> things;
    private ActorRef thingsActor;
    private CompletionStage completionStage;

    @BeforeEach
    public void setUp() {
        actorSystemProvider = mock(RefCountResource.class);
        pattern = mock(AkkaProtocolPattern.class);
        triple = mock(Triple.class);
        things = mock(Map.class);
        thingsActor = mock(ActorRef.class);
        thing = mock(ExposedThing.class);
        completionStage = mock(CompletionStage.class);
    }

    @Test
    public void exposeShouldSendExposeMessageToThingsActor() {
        when(triple.second()).thenReturn(things);
        when(triple.third()).thenReturn(thingsActor);
        when(pattern.ask(any(ActorRef.class), any(), any())).thenReturn(completionStage);
        when(completionStage.thenRun(any())).thenReturn(completionStage);

        AkkaProtocolServer server = new AkkaProtocolServer(actorSystemProvider, pattern, triple);
        server.expose(thing);

        verify(pattern, timeout(1 * 1000L)).ask(eq(thingsActor), any(ThingsActor.Expose.class), any());
    }
}