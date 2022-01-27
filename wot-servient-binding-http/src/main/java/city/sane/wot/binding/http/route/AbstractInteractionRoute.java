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
package city.sane.wot.binding.http.route;

import city.sane.wot.Servient;
import city.sane.wot.thing.ExposedThing;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Objects;

abstract class AbstractInteractionRoute extends AbstractRoute {
    protected final Map<String, ExposedThing> things;
    private final Servient servient;
    private final String securityScheme;

    public AbstractInteractionRoute(Servient servient, String securityScheme,
                                    Map<String, ExposedThing> things) {
        this.servient = servient;
        this.securityScheme = securityScheme;
        this.things = things;
    }

    @Override
    public Object handle(Request request, Response response) {
        logRequest(request);

        String requestContentType = getOrDefaultRequestContentType(request);

        String unsupportedMediaTypeResponse = unsupportedMediaTypeResponse(response, requestContentType);
        if (unsupportedMediaTypeResponse != null) {
            return unsupportedMediaTypeResponse;
        }

        String id = request.params(":id");
        String name = request.params(":name");

        ExposedThing thing = things.get(id);
        if (thing != null) {
            // Thing Interaction - Access Control
            if (!checkCredentials(securityScheme, thing.getId(), request)) {
                response.header(HttpHeader.WWW_AUTHENTICATE.asString(), securityScheme + " realm=\"" + id + "\"");
                response.status(HttpStatus.UNAUTHORIZED_401);
                return "Unauthorized";
            }

            return handleInteraction(request, response, requestContentType, name, thing);
        }
        else {
            response.status(HttpStatus.NOT_FOUND_404);
            return "Thing not found";
        }
    }

    private boolean checkCredentials(String securityScheme, String id, Request request) {
        if (securityScheme == null) {
            // no security configured -> always authorized
            return true;
        }

        log.debug("HttpServer checking credentials for '{}'", id);
        Object credentials = servient.getCredentials(id);

        if (securityScheme.equals("Basic")) {
            String header = request.headers(HttpHeader.AUTHORIZATION.asString());
            if (header != null) {
                String decodedHeader = new String(Base64.decodeBase64(header.replaceAll("^(?i)basic ", "")));
                String[] split = decodedHeader.split(":", 2);
                if (split.length >= 2) {
                    String requiredUsername = (String) ((Map) credentials).get("username");
                    String requiredPassword = (String) ((Map) credentials).get("password");
                    String givenUsername = split[0];
                    String givenPassword = split[1];

                    return Objects.equals(requiredUsername, givenUsername) && Objects.equals(requiredPassword, givenPassword);
                }
                else {
                    log.warn("Unable to decode username and password from authorization header");
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else if (securityScheme.equals("Bearer")) {
            String header = request.headers(HttpHeader.AUTHORIZATION.asString());
            if (header != null) {
                String decodedHeader = header.replaceAll("^(?i)bearer ", "");
                String requiredToken = (String) ((Map) credentials).get("token");
                String givenToken = decodedHeader;

                return Objects.equals(requiredToken, givenToken);
            }
            else {
                return false;
            }
        }
        else {
            log.warn("Unknown security scheme provided. Decline access.");
            return false;
        }
    }

    protected abstract Object handleInteraction(Request request,
                                                Response response,
                                                String requestContentType,
                                                String name,
                                                ExposedThing thing);
}
