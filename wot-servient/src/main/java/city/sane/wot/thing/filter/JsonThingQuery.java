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

import city.sane.wot.thing.Thing;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Allows filtering of things during discovery process using a JSON query.
 * <p>
 * Example Query: {"@type":"https://www.w3.org/2019/wot/td#Thing"}
 * </p>
 */
public class JsonThingQuery implements ThingQuery {
    private final String query;

    public JsonThingQuery(String query) {
        this.query = query;
    }

    JsonThingQuery() {
        // required by jackson
        query = null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(query);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JsonThingQuery that = (JsonThingQuery) o;
        return Objects.equals(query, that.query);
    }

    @Override
    public String toString() {
        return "JsonThingQuery{" +
                "sparql=" + query +
                '}';
    }

    @Override
    public List<Thing> filter(Collection<Thing> things) throws ThingQueryException {
        return getSparqlQuery().filter(things);
    }

    SparqlThingQuery getSparqlQuery() throws ThingQueryException {
        try {
            StringReader reader = new StringReader(query);
            Model frame = Rio.parse(reader, "", RDFFormat.JSONLD);
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            Rio.write(frame, o, RDFFormat.NTRIPLES);

            String sparlQueryString = o.toString().replace("_:", "?");

            if (sparlQueryString.isEmpty()) {
                throw new ThingQueryException("Translated SPARQL Query is empty. Something seems wrong with your JSON query: " + query);
            }

            return new SparqlThingQuery(sparlQueryString);
        }
        catch (IOException e) {
            throw new ThingQueryException(e);
        }
    }

    public String getQuery() {
        return query;
    }
}
