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
import com.typesafe.config.Config;
import scala.compat.java8.FutureConverters;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a Singleton class, which is used by {@link AkkaProtocolClient} and {@link
 * AkkaProtocolServer} to share a single ActorSystem.
 */
public class SharedActorSystemProvider {
    private static RefCountResource<Triple<ActorSystem, Map<String, ExposedThing>, ActorRef>> singleton = null;

    private SharedActorSystemProvider() {
        // singleton class
    }

    public static synchronized RefCountResource<Triple<ActorSystem, Map<String, ExposedThing>, ActorRef>> singleton(
            Config config) {
        if (singleton == null) {
            singleton = new RefCountResource<>(
                    () -> {
                        ActorSystem system = ActorSystem.create(config.getString("wot.servient.akka.system-name"), config.getConfig("wot.servient"));
                        Map<String, ExposedThing> things = new HashMap<>();
                        ActorRef thingsActor = system.actorOf(ThingsActor.props(things), "things");

                        return new Triple(system, things, thingsActor);
                    },
                    triple -> FutureConverters.toJava(triple.first().terminate()).toCompletableFuture().join()
            );
        }
        return singleton;
    }
}
