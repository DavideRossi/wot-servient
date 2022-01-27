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
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This Agent is started together with {@link city.sane.wot.binding.jadex.JadexProtocolServer} and
 * is responsible for exposing things. For each exposed Thing a {@link ThingAgent} is created, which
 * is responsible for the interaction with the Thing.
 */
@Agent
@ProvidedServices({
        @ProvidedService(type = ThingsService.class, scope = ServiceScope.PLATFORM)
})
public class ThingsAgent implements ThingsService {
    private static final Logger log = LoggerFactory.getLogger(ThingsAgent.class);
    private final Map<String, IExternalAccess> children;
    @Agent
    private IInternalAccess agent;
    @AgentArgument("things")
    private Map<String, ExposedThing> things;

    public ThingsAgent() {
        children = new HashMap<>();
    }

    ThingsAgent(IInternalAccess agent,
                Map<String, ExposedThing> things,
                Map<String, IExternalAccess> children) {
        this.agent = agent;
        this.things = things;
        this.children = children;
    }

    @AgentCreated
    public void created() {
        log.debug("Agent created");
    }

    @Override
    public IFuture<IExternalAccess> expose(String id) {
        CreationInfo info = new CreationInfo()
                .setFilenameClass(ThingAgent.class)
                .addArgument("thing", things.get(id));
        IFuture<IExternalAccess> component = agent.createComponent(info);
        component.addResultListener(new IResultListener<>() {
            @Override
            public void resultAvailable(IExternalAccess thingAgent) {
                children.put(id, thingAgent);
            }

            @Override
            public void exceptionOccurred(Exception e) {
                // do nothing
            }
        });

        return component;
    }

    @Override
    public IFuture<Void> destroy(String id) {
        IExternalAccess thingAgent = children.get(id);

        Future<Void> future = new Future();
        thingAgent.killComponent().addResultListener(new IResultListener<Map<String, Object>>() {
            @Override
            public void resultAvailable(Map<String, Object> result) {
                future.setResult(null);
            }

            @Override
            public void exceptionOccurred(Exception exception) {
                future.setException(exception);
            }
        });

        return future;
    }
}
