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

import city.sane.wot.DefaultWot;
import city.sane.wot.Wot;
import city.sane.wot.WotException;
import city.sane.wot.thing.ConsumedThing;

import java.util.concurrent.ExecutionException;

/**
 * Consume thing description from string and then interact with the thing via coap.
 */
@SuppressWarnings({ "java:S106" })
class CfSandboxClient {
    public static void main(String[] args) throws ExecutionException, InterruptedException, WotException {
        // create wot
        Wot wot = DefaultWot.clientOnly();

        String thing = "{\n" +
                "    \"@context \": \"https://www.w3.org/2019/td/v1\",\n" +
                "    \"id\": \"urn:dev:wot:org:eclipse:cf-sandbox\",\n" +
                "    \"title\": \"Cf-Sandbox\",\n" +
                "    \"description\": \"Californium online example server (coap://californium.eclipse.org/)\",\n" +
                "    \"securityDefinitions\": {\n" +
                "        \"none\": {\"scheme\": \"nosec\"}\n" +
                "    },\n" +
                "    \"security\": \"none\",\n" +
                "    \"properties\": {\n" +
                "        \"test\": {\n" +
                "            \"description\": \"Test CoAP resource\",\n" +
                "            \"type\": \"string\",\n" +
                "            \"forms\": [\n" +
                "                {\n" +
                "                    \"href\": \"coap://californium.eclipse.org:5683/test\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";

        ConsumedThing consumedThing = wot.consume(thing);

        Object value = consumedThing.getProperty("test").read().get();

        System.out.println("CfSandboxClient: Received: " + value);
    }
}
