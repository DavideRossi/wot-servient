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

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import city.sane.wot.binding.akka.Message;
import city.sane.wot.content.Content;
import io.reactivex.rxjava3.core.Observer;

import static java.util.Objects.requireNonNull;

/**
 * This actor is temporarily created for a obersavtion of an event/a property.
 */
public class ObserveActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Observer<Content> observer;
    private final ActorSelection selection;
    private final Message subscribeMessage;

    public ObserveActor(Observer<Content> observer,
                        ActorSelection selection,
                        Message subscribeMessage) {
        this.observer = observer;
        this.selection = selection;
        this.subscribeMessage = subscribeMessage;
    }

    @Override
    public void preStart() {
        log.debug("Started");

        log.debug("Send Subscribe message to {}", selection);
        selection.tell(subscribeMessage, getSelf());
    }

    @Override
    public void postStop() {
        log.debug("Stop");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.SubscriptionNext.class, m -> observer.onNext(m.content))
                .match(Message.SubscriptionError.class, m -> {
                    observer.onError(m.e);
                    getContext().stop(getSelf());
                })
                .match(Message.SubscriptionComplete.class, m -> {
                    observer.onComplete();
                    getContext().stop(getSelf());
                })
                .build();
    }

    public static Props props(Observer<Content> observer,
                              ActorSelection selection,
                              Message subscribeMessage) {
        return Props.create(ObserveActor.class, () -> new ObserveActor(requireNonNull(observer), requireNonNull(selection), requireNonNull(subscribeMessage)));
    }
}
