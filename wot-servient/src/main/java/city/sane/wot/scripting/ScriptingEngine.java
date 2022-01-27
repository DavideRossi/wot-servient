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
package city.sane.wot.scripting;

import city.sane.wot.Wot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * A ScriptingEngine describes how a WoT script can be executed in a certain scripting language.
 */
interface ScriptingEngine {
    /**
     * Returns the media type supported by the codec (e.g. application/javascript).
     *
     * @return
     */
    String getMediaType();

    /**
     * Returns the file extension supported by the codec (e.g. .js).
     *
     * @return
     */
    String getFileExtension();

    /**
     * Runs <code>script</code> in sandboxed context.
     *
     * @param script
     * @param wot
     * @param executorService
     * @return
     */
    CompletableFuture<Void> runScript(String script, Wot wot, ExecutorService executorService);

    /**
     * Runs <code>script</code> in privileged context (dangerous) - means here: Script can import
     * classes and make system calls.
     *
     * @param script
     * @param wot
     * @param executorService
     * @return
     */
    CompletableFuture<Void> runPrivilegedScript(String script,
                                                Wot wot,
                                                ExecutorService executorService);
}
