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
package city.sane.wot.thing.filter;

import city.sane.wot.thing.Context;
import city.sane.wot.thing.Thing;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class JsonThingQueryTest {
    @Test
    public void constructorShouldTranslateJsonQueryToCorrectSparqlQuery() throws ThingQueryException {
        JsonThingQuery query = new JsonThingQuery("{\"@type\":\"https://www.w3.org/2019/wot/td#Thing\"}");

        MatcherAssert.assertThat(
                query.getSparqlQuery().getQuery(),
                matchesPattern("\\?genid.* <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://www.w3.org/2019/wot/td#Thing> .\n")
        );
    }

    @Test
    public void filterShouldReturnOnlyMatchingThings() throws ThingQueryException {
        List<Thing> things = new ArrayList<>();
        things.add(new Thing.Builder()
                .setObjectContext(new Context("https://www.w3.org/2019/wot/td/v1"))
                .setObjectType("Thing")
                .setId("KlimabotschafterWetterstationen:Stellingen")
                .build());
        for (int i = 0; i < 10; i++) {
            things.add(new Thing.Builder()
                    .setObjectContext(new Context("https://www.w3.org/2019/wot/td/v1"))
                    .setId("luftdaten.info:" + i)
                    .build());
        }

        ThingQuery query = new JsonThingQuery("{\"@type\":\"https://www.w3.org/2019/wot/td#Thing\"}");
        Collection<Thing> filtered = query.filter(things);

        assertEquals(1, filtered.size());
    }

    @Test
    public void testEquals() {
        JsonThingQuery queryA = new JsonThingQuery("{\"@type\":\"https://www.w3.org/2019/wot/td#Thing\"}");
        JsonThingQuery queryB = new JsonThingQuery("{\"@type\":\"https://www.w3.org/2019/wot/td#Thing\"}");
        JsonThingQuery queryC = new JsonThingQuery("{\"http://www.w3.org/ns/td#title\":\"Klimabotschafter:Rahlstedt\"}");

        assertEquals(queryA, queryB);
        assertEquals(queryB, queryA);
        assertNotEquals(queryA, queryC);
        assertNotEquals(queryC, queryA);
    }
}