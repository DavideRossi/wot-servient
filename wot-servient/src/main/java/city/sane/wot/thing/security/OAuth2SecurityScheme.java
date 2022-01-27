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

import java.util.List;

/**
 * OAuth2 authentication security configuration for systems conformant with !RFC6749 and !RFC8252,
 * identified by the term oauth2 (i.e., "scheme": "oauth2"). For the implicit flow authorization
 * MUST be included. For the password and client flows token MUST be included. For the code flow
 * both authorization and token MUST be included. If no scopes are defined in the SecurityScheme
 * then they are considered to be empty.<br> See also: https://www.w3.org/2019/wot/security#oauth2securityscheme
 */
public class OAuth2SecurityScheme implements SecurityScheme {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String authorization;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String flow;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String token;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String refresh;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> scopes;

    public OAuth2SecurityScheme(String authorization,
                                String flow,
                                String token,
                                String refresh,
                                List<String> scopes) {
        this.authorization = authorization;
        this.flow = flow;
        this.token = token;
        this.refresh = refresh;
        this.scopes = scopes;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getFlow() {
        return flow;
    }

    public String getToken() {
        return token;
    }

    public String getRefresh() {
        return refresh;
    }

    public List<String> getScopes() {
        return scopes;
    }

    @Override
    public String toString() {
        return "OAuth2SecurityScheme{" +
                "authorization='" + authorization + '\'' +
                ", flow='" + flow + '\'' +
                ", token='" + token + '\'' +
                ", refresh='" + refresh + '\'' +
                ", scopes=" + scopes +
                '}';
    }
}
