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
package city.sane.wot.thing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a JSON-LD context.
 */
@JsonDeserialize(using = ContextDeserializer.class)
@JsonSerialize(using = ContextSerializer.class)
public class Context {
    private final Map<String, String> urls = new HashMap<>();

    public Context() {
    }

    public Context(String url) {
        addContext(url);
    }

    public Context addContext(String url) {
        return addContext(null, url);
    }

    public Context addContext(String prefix, String url) {
        urls.put(prefix, url);
        return this;
    }

    public Context(String prefix, String url) {
        addContext(prefix, url);
    }

    @Override
    public int hashCode() {
        return getUrls().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Context)) {
            return false;
        }
        return getUrls().equals(((Context) obj).getUrls());
    }

    @Override
    public String toString() {
        return "Context{" +
                "urls=" + urls +
                '}';
    }

    private Map<String, String> getUrls() {
        return urls;
    }

    public String getDefaultUrl() {
        return getUrl(null);
    }

    public String getUrl(String prefix) {
        return urls.get(prefix);
    }

    public Map<String, String> getPrefixedUrls() {
        return urls.entrySet().stream().filter(e -> e.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
