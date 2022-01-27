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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Describes properties of a security mechanism (e.g. password authentication).<br> See also:
 * https://www.w3.org/TR/wot-thing-description/#security-serialization-json
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "scheme")
@JsonSubTypes({
        @JsonSubTypes.Type(value = APIKeySecurityScheme.class, name = "apikey"),
        @JsonSubTypes.Type(value = BasicSecurityScheme.class, name = "basic"),
        @JsonSubTypes.Type(value = BearerSecurityScheme.class, name = "bearer"),
        @JsonSubTypes.Type(value = CertSecurityScheme.class, name = "cert"),
        @JsonSubTypes.Type(value = DigestSecurityScheme.class, name = "digest"),
        @JsonSubTypes.Type(value = NoSecurityScheme.class, name = "nosec"),
        @JsonSubTypes.Type(value = OAuth2SecurityScheme.class, name = "oauth2"),
        @JsonSubTypes.Type(value = PoPSecurityScheme.class, name = "pop"),
        @JsonSubTypes.Type(value = PSKSecurityScheme.class, name = "psk"),
        @JsonSubTypes.Type(value = PublicSecurityScheme.class, name = "public")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public interface SecurityScheme {
}
