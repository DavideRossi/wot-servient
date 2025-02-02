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
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.action.ExposedThingAction;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Endpoint for invoking a {@link city.sane.wot.thing.action.ThingAction}.
 */
public class InvokeActionRoute extends AbstractInteractionRoute {
    static final Logger log = LoggerFactory.getLogger(InvokeActionRoute.class);

    public InvokeActionRoute(Servient servient, String securityScheme,
                             Map<String, ExposedThing> things) {
        super(servient, securityScheme, things);
    }

    @Override
    protected Object handleInteraction(Request request,
                                       Response response,
                                       String requestContentType,
                                       String name,
                                       ExposedThing thing) {
        ExposedThingAction<Object, Object> action = thing.getAction(name);
        if (action != null) {
            try {
                Content content = new Content(requestContentType, request.bodyAsBytes());
                Object input = ContentManager.contentToValue(content, action.getInput());

                Map<String, Map<String, Object>> options = Map.of(
                        "uriVariables", parseUrlParameters(request.queryMap().toMap(), action.getUriVariables())
                );

                Object value = action.invoke(input, options).get();

                return respondWithValue(response, requestContentType, content, value);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            catch (ContentCodecException | ExecutionException e) {
                response.status(HttpStatus.SERVICE_UNAVAILABLE_503);
                return e;
            }
        }
        else {
            response.status(HttpStatus.NOT_FOUND_404);
            return "Action not found";
        }
    }

    private Map<String, Object> parseUrlParameters(Map<String, String[]> urlParams,
                                                   Map<String, Map<String, Object>> uriVariables) {
        log.debug("parse url parameters '{}' with uri variables '{}'", urlParams.keySet(), uriVariables);
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : urlParams.entrySet()) {
            String name = entry.getKey();
            String[] urlValue = entry.getValue();

            Map uriVariable = uriVariables.get(name);
            if (uriVariable != null) {
                Object type = uriVariable.get("type");

                if (type != null) {
                    if (type.equals("integer") || type.equals("number")) {
                        Integer value = Integer.valueOf(urlValue[0]);
                        params.put(name, value);
                    }
                    else if (type.equals("string")) {
                        String value = urlValue[0];
                        params.put(name, value);
                    }
                    else {
                        log.warn("Not able to read variable '{}' because variable type '{}' is unknown", name, type);
                    }
                }
            }
        }
        return params;
    }

    private Object respondWithValue(Response response,
                                    String requestContentType,
                                    Content content,
                                    Object value) {
        try {
            Content outputContent = ContentManager.valueToContent(value, requestContentType);

            if (value != null) {
                response.type(content.getType());
                return outputContent;
            }
            else {
                return "";
            }
        }
        catch (ContentCodecException e) {
            response.status(HttpStatus.SERVICE_UNAVAILABLE_503);
            return e;
        }
    }
}
