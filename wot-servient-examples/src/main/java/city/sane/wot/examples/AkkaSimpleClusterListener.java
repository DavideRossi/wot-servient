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
package city.sane.wot.examples;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import city.sane.wot.Servient;
import city.sane.wot.ServientException;
import city.sane.wot.binding.akka.AkkaProtocolServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This example lists the members found in the Akka Cluster.
 */
class AkkaSimpleClusterListener extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Cluster cluster = Cluster.get(getContext().getSystem());

    // subscribe to cluster changes
    @Override
    public void preStart() {
        // #subscribe
        cluster.subscribe(getSelf(), MemberEvent.class, UnreachableMember.class);
        // #subscribe
    }

    // re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        MemberUp.class,
                        mUp -> log.debug("Member is Up: {}", mUp.member()))
                .match(
                        UnreachableMember.class,
                        mUnreachable -> log.debug("Member detected as unreachable: {}", mUnreachable.member()))
                .match(
                        MemberRemoved.class,
                        mRemoved -> log.debug("Member is Removed: {}", mRemoved.member()))
                .match(
                        MemberEvent.class,
                        message -> {
                            // ignore
                        })
                .build();
    }

    public static void main(String[] args) throws ServientException {
        Config config = ConfigFactory.load();

        Servient servient = new Servient(config);
        servient.start().join();

        AkkaProtocolServer server = servient.getServer(AkkaProtocolServer.class);
        ActorSystem system = server.getActorSystem();

        system.actorOf(Props.create(AkkaSimpleClusterListener.class, AkkaSimpleClusterListener::new));
    }
}