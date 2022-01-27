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
package city.sane.wot.binding.akka.actor;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;
import city.sane.wot.binding.akka.Message;
import city.sane.wot.binding.akka.actor.ThingActor.GetThingDescription;
import city.sane.wot.binding.akka.actor.ThingActor.InvokeAction;
import city.sane.wot.binding.akka.actor.ThingActor.InvokedAction;
import city.sane.wot.binding.akka.actor.ThingActor.PropertiesValues;
import city.sane.wot.binding.akka.actor.ThingActor.ReadAllProperties;
import city.sane.wot.binding.akka.actor.ThingActor.ReadProperty;
import city.sane.wot.binding.akka.actor.ThingActor.ReadPropertyResponse;
import city.sane.wot.binding.akka.actor.ThingActor.SubscribeEvent;
import city.sane.wot.binding.akka.actor.ThingActor.SubscribeProperty;
import city.sane.wot.binding.akka.actor.ThingActor.ThingDescription;
import city.sane.wot.binding.akka.actor.ThingActor.WriteProperty;
import city.sane.wot.binding.akka.actor.ThingActor.WrittenProperty;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.action.ExposedThingAction;
import city.sane.wot.thing.event.ExposedThingEvent;
import city.sane.wot.thing.property.ExposedThingProperty;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThingActorTest {
    private ActorSystem system;
    private ExposedThing thing;
    private Duration timeout;
    private ExposedThingProperty<Object> property;
    private ExposedThingEvent<Object> event;
    private ExposedThingAction<Object, Object> action;

    @BeforeEach
    public void setUp() {
        system = ActorSystem.create();
        thing = mock(ExposedThing.class);
        timeout = Duration.ofMillis(3000);
        property = mock(ExposedThingProperty.class);
        event = mock(ExposedThingEvent.class);
        action = mock(ExposedThingAction.class);
    }

    @AfterEach
    public void tearDown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void actorCreationShouldAddFormsToThing() {
        when(thing.getId()).thenReturn("counter");
        when(thing.getProperties()).thenReturn(Map.of("count", property));
        when(property.isObservable()).thenReturn(true);
        when(thing.getActions()).thenReturn(Map.of("reset", action));
        when(thing.getEvents()).thenReturn(Map.of("changed", event));

        final Props props = ThingActor.props(thing);
        TestActorRef.create(system, props);

        verify(thing, timeout(1 * 1000L)).addForm(any());
        verify(property, timeout(1 * 1000L)).addForm(any());
        verify(action, timeout(1 * 1000L)).addForm(any());
        verify(event, timeout(1 * 1000L)).addForm(any());
    }

    @Test
    public void getThingDescriptionShouldBeAnsweredWithThingDescription() throws ExecutionException, InterruptedException, ContentCodecException {
        ExposedThing exposedThing = new ExposedThing(null);

        final Props props = ThingActor.props(exposedThing);
        final TestActorRef<ThingActor> ref = TestActorRef.create(system, props);

        final CompletableFuture<Object> future = ask(ref, new GetThingDescription(), timeout).toCompletableFuture();

        assertEquals(
                new ThingDescription(ContentManager.valueToContent(exposedThing)),
                future.get()
        );
    }

    @Test
    public void readAllPropertiesShouldBeAnsweredWithProperties() throws ExecutionException, InterruptedException, ContentCodecException {
        when(thing.readProperties()).thenReturn(completedFuture(Map.of("count", 1337)));

        final Props props = ThingActor.props(thing);
        final TestActorRef<ThingActor> ref = TestActorRef.create(system, props);

        final CompletableFuture<Object> future = ask(ref, new ReadAllProperties(), timeout).toCompletableFuture();

        verify(thing).readProperties();
        assertEquals(
                new PropertiesValues(ContentManager.valueToContent(Map.of("count", 1337))),
                future.get()
        );
    }

    @Test
    public void readPropertyShouldBeAnsweredWithReadPropertyDone() throws ExecutionException, InterruptedException, ContentCodecException {
        when(thing.getProperty(any())).thenReturn(property);
        when(property.read()).thenReturn(completedFuture(42));

        final Props props = ThingActor.props(thing);
        final TestActorRef<ThingActor> ref = TestActorRef.create(system, props);

        final CompletableFuture<Object> future = ask(ref, new ReadProperty("count"), timeout).toCompletableFuture();

        verify(property).read();
        assertEquals(
                new ReadPropertyResponse(ContentManager.valueToContent(42)),
                future.get()
        );
    }

    @Test
    public void writePropertyShouldBeAnsweredWithWrittenProperty() throws ExecutionException, InterruptedException, ContentCodecException {
        when(thing.getProperty(any())).thenReturn(property);
        when(property.getClassType()).thenReturn(Object.class);
        when(property.write(any())).thenReturn(completedFuture(42));

        final Props props = ThingActor.props(thing);
        final TestActorRef<ThingActor> ref = TestActorRef.create(system, props);

        final CompletableFuture<Object> future = ask(ref, new WriteProperty("count", ContentManager.valueToContent(1337)), timeout).toCompletableFuture();

        verify(property).write(1337);
        assertEquals(
                new WrittenProperty(ContentManager.valueToContent(42)),
                future.get()
        );
    }

    @Test
    public void subscribePropertyShouldBeAnsweredWithSubscribtionNext() throws ExecutionException, InterruptedException, ContentCodecException {
        when(thing.getProperty(any())).thenReturn(property);
        when(property.observer()).thenReturn(Observable.just(Optional.of(43)));

        final Props props = ThingActor.props(thing);
        final TestActorRef<ThingActor> ref = TestActorRef.create(system, props);

        final CompletableFuture<Object> future = ask(ref, new SubscribeProperty("count"), timeout).toCompletableFuture();

        verify(property).observer();
        assertEquals(
                new Message.SubscriptionNext(ContentManager.valueToContent(43)),
                future.get()
        );
    }

    @Test
    public void subscribeEventShouldBeAnsweredWithSubscribtionNext() throws ExecutionException, InterruptedException, ContentCodecException {
        when(thing.getEvent(any())).thenReturn(event);
        when(event.observer()).thenReturn(Observable.just(Optional.of(43)));

        final Props props = ThingActor.props(thing);
        final TestActorRef<ThingActor> ref = TestActorRef.create(system, props);

        final CompletableFuture<Object> future = ask(ref, new SubscribeEvent("change"), timeout).toCompletableFuture();

        verify(event).observer();
        assertEquals(
                new Message.SubscriptionNext(ContentManager.valueToContent(43)),
                future.get()
        );
    }

    @Test
    public void invokeActionShouldBeAnsweredWithInvokedAction() throws ExecutionException, InterruptedException, ContentCodecException {
        when(thing.getAction(any())).thenReturn(action);
        when(action.invoke(any())).thenReturn(completedFuture(null));

        final Props props = ThingActor.props(thing);
        final TestActorRef<ThingActor> ref = TestActorRef.create(system, props);

        final CompletableFuture<Object> future = ask(ref, new InvokeAction("reset"), timeout).toCompletableFuture();

        verify(action).invoke(null);
        assertEquals(
                new InvokedAction(ContentManager.valueToContent(null)),
                future.get()
        );
    }
}