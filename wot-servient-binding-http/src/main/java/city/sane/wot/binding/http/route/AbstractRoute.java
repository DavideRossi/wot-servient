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

import city.sane.wot.content.ContentManager;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static city.sane.wot.util.LoggingUtil.sanitizeLogArg;

/**
 * Abstract route for exposing Things. Inherited from all other routes.
 */
abstract class AbstractRoute implements Route {
    static final Logger log = LoggerFactory.getLogger(AbstractRoute.class);

    String getOrDefaultRequestContentType(Request request) {
        if (request.contentType() != null) {
            return request.contentType();
        }
        else {
            return ContentManager.DEFAULT;
        }
    }

    String unsupportedMediaTypeResponse(Response response, String requestContentType) {
        if (!ContentManager.isSupportedMediaType(requestContentType)) {
            log.warn("Unsupported media type: {}", sanitizeLogArg(requestContentType));
            response.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415);
            return "Unsupported Media Type (supported: " + String.join(", ", ContentManager.getSupportedMediaTypes()) + ")";
        }
        else {
            return null;
        }
    }

    void logRequest(Request request) {
        if (log.isDebugEnabled()) {
            log.debug("Handle {} to '{}'", request.requestMethod(), request.url());
            if (request.raw().getQueryString() != null && !request.raw().getQueryString().isEmpty()) {
                log.debug("Request parameters: {}", request.raw().getQueryString());
            }
        }
    }
}
