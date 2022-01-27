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
 * Interacts with a Thing that is secured with HTTP Basic Auth.
 * <p>
 * application.conf:
 * <p>
 * wot { servient { credentials { "urn:dev:wot:http:auth:basic" = { username = "guest" password =
 * "guest" } } } }
 */
@SuppressWarnings({ "java:S106" })
class HttpBasicAuthClient {
    public static void main(String[] args) throws ExecutionException, InterruptedException, WotException {
        Wot wot = DefaultWot.clientOnly();

        String thing = "{\n" +
                "    \"@context\": \"https://www.w3.org/2019/td/v1\",\n" +
                "    \"title\": \"HTTP Basic Auth\",\n" +
                "    \"id\": \"urn:dev:wot:http:auth:basic\",\n" +
                "    \"securityDefinitions\": {\n" +
                "        \"basic_sc\": {\n" +
                "            \"scheme\": \"basic\",\n" +
                "            \"in\": \"header\"\n" +
                "        }\n" +
                "    }," +
                "    \"security\": [\"basic_sc\"],\n" +
                "    \"actions\" : {\n" +
                "        \"fire\": {\n" +
                "            \"forms\": [\n" +
                "                {\"href\": \"https://jigsaw.w3.org/HTTP/Basic/\", \"htv:methodName\": \"GET\"}\n" +
                "            ]\n" +
                "        }\n" +
                "    } \n" +
                "}";

        ConsumedThing consumedThing = wot.consume(thing);

        Object output = consumedThing.getAction("fire").invoke().get();
        System.out.println(output);
    }
}
