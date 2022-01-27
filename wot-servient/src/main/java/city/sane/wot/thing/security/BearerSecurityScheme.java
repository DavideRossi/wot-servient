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
 * Bearer token authentication security configuration identified by the term bearer (i.e., "scheme":
 * "bearer"). This scheme is intended for situations where bearer tokens are used independently of
 * OAuth2. If the oauth2 scheme is specified it is not generally necessary to specify this scheme as
 * well as it is implied. For format, the value jwt indicates conformance with RFC7519, jws
 * indicates conformance with RFC7797, cwt indicates conformance with RFC8392, and jwe indicates
 * conformance with !RFC7516, with values for alg interpreted consistently with those standards.
 * Other formats and algorithms for bearer tokens MAY be specified in vocabulary extensions.<br> See
 * also: https://www.w3.org/2019/wot/security#bearersecurityscheme
 */
public class BearerSecurityScheme implements SecurityScheme {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String in;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String alg;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String format;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String authorization;

    public BearerSecurityScheme() {
        this(null, null, null, null, null);
    }

    public BearerSecurityScheme(String in,
                                String alg,
                                String format,
                                String name,
                                String authorization) {
        this.in = in;
        this.alg = alg;
        this.format = format;
        this.name = name;
        this.authorization = authorization;
    }

    public String getIn() {
        return in;
    }

    public String getAlg() {
        return alg;
    }

    public String getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public String getAuthorization() {
        return authorization;
    }

    @Override
    public String toString() {
        return "BearerSecurityScheme{" +
                "in='" + in + '\'' +
                ", alg='" + alg + '\'' +
                ", format='" + format + '\'' +
                ", name='" + name + '\'' +
                ", authorization='" + authorization + '\'' +
                '}';
    }
}
