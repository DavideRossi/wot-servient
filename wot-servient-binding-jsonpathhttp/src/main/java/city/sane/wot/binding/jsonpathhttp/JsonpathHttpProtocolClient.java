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
package city.sane.wot.binding.jsonpathhttp;

import city.sane.wot.binding.http.HttpProtocolClient;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Allows consuming Things via HTTP. The return values can be manipulated with JSON queries.
 */
public class JsonpathHttpProtocolClient extends HttpProtocolClient {
    private static final Logger log = LoggerFactory.getLogger(JsonpathHttpProtocolClient.class);

    @Override
    public CompletableFuture<Content> readResource(Form form) {
        // create form with http protocol href
        Form httpForm = new Form.Builder(form)
                .setHref(form.getHref().replace("jsonpath+http", "http"))
                .build();

        return super.readResource(httpForm).thenApply(content -> {
            if (form.getOptional("sane:jsonPath") != null) {
                try {
                    // apply jsonpath on content
                    String json = new String(content.getBody());

                    String jsonPath = form.getOptional("sane:jsonPath").toString();
                    Object newValue = JsonPath.read(json, jsonPath);

                    return ContentManager.valueToContent(newValue, "application/json");
                }
                catch (InvalidPathException | ContentCodecException e) {
                    log.warn("Unable apply JSON Path", e);
                    return content;
                }
            }
            else {
                log.warn("No JSON Path found in form '{}'", form);
                return content;
            }
        });
    }
}
