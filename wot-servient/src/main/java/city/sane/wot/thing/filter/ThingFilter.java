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

import java.net.URI;
import java.util.Objects;

/**
 * ThingFilter is used for the discovery process and specifies what things to look for and where to
 * look for them.
 */
public class ThingFilter {
    private DiscoveryMethod method;
    private URI url;
    private ThingQuery query;

    public ThingFilter() {
        this(DiscoveryMethod.ANY);
    }

    public ThingFilter(DiscoveryMethod method) {
        this.method = method;
    }

    public DiscoveryMethod getMethod() {
        return method;
    }

    /**
     * Specifies where to search for Things. {@link DiscoveryMethod#LOCAL} searches only on the
     * local {@link city.sane.wot.Servient}, {@link DiscoveryMethod#DIRECTORY} searches in the Thing
     * Directory defined in {@link #url}. {@link DiscoveryMethod#ANY} uses the discovery mechanisms
     * provided by all {@link city.sane.wot.binding.ProtocolClient} implementations to consider all
     * available Things.
     *
     * @param method
     * @return
     */
    public ThingFilter setMethod(DiscoveryMethod method) {
        this.method = method;
        return this;
    }

    public URI getUrl() {
        return url;
    }

    /**
     * Used in combination with {@link DiscoveryMethod#DIRECTORY} and defines the URL of the Thing
     * Directory to be searched in.
     *
     * @param url
     * @return
     */
    public ThingFilter setUrl(URI url) {
        this.url = url;
        return this;
    }

    public ThingQuery getQuery() {
        return query;
    }

    /**
     * Defines a query that filters the Things found according to certain properties.<br> See also
     * {@link SparqlThingQuery} and {@link JsonThingQuery}
     *
     * @param query
     * @return
     */
    public ThingFilter setQuery(ThingQuery query) {
        this.query = query;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, url, query);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThingFilter that = (ThingFilter) o;
        return method == that.method &&
                Objects.equals(url, that.url) &&
                Objects.equals(query, that.query);
    }

    @Override
    public String toString() {
        return "ThingFilter{" +
                "method=" + method +
                ", url=" + url +
                ", query=" + query +
                '}';
    }
}
