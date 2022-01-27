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

/**
 * Digest authentication security configuration identified by the term digest (i.e., "scheme":
 * "digest"). This scheme is similar to basic authentication but with added features to avoid
 * man-in-the-middle attacks.<br> See also: https://www.w3.org/2019/wot/security#digestsecurityscheme
 */
public class DigestSecurityScheme implements SecurityScheme {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String in;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String qop;

    public DigestSecurityScheme(String in, String name, String qop) {
        this.in = in;
        this.name = name;
        this.qop = qop;
    }

    public String getIn() {
        return in;
    }

    public String getName() {
        return name;
    }

    public String getQop() {
        return qop;
    }

    @Override
    public String toString() {
        return "DigestSecurityScheme{" +
                "in='" + in + '\'' +
                ", name='" + name + '\'' +
                ", qop='" + qop + '\'' +
                '}';
    }
}
