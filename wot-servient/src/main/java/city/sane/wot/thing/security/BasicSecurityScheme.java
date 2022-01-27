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
package city.sane.wot.thing.security;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Basic authentication security configuration identified by the term basic (i.e., "scheme":
 * "basic"), using an unencrypted username and password. This scheme should be used with some other
 * security mechanism providing confidentiality, for example, TLS.<br> See also:
 * https://www.w3.org/2019/wot/security#basicsecurityscheme
 */
public class BasicSecurityScheme implements SecurityScheme {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String in;

    public BasicSecurityScheme() {
        this(null);
    }

    public BasicSecurityScheme(String in) {
        this.in = in;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIn());
    }

    public String getIn() {
        return in;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasicSecurityScheme)) {
            return false;
        }
        BasicSecurityScheme that = (BasicSecurityScheme) o;
        return Objects.equals(getIn(), that.getIn());
    }

    @Override
    public String toString() {
        return "BasicSecurityScheme{" +
                "in='" + in + '\'' +
                '}';
    }
}
