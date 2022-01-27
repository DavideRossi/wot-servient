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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScriptingManagerTest {
    @BeforeEach
    public void setUp() {
        ScriptingManager.addEngine(new MyScriptingEngine());
    }

    @Test
    public void runScript(@TempDir Path folder) throws IOException {
        File file = Paths.get(folder.toString(), "counter.test").toFile();
        java.nio.file.Files.writeString(file.toPath(), "1+1");

        ScriptingManager.runScript(file, null);

        // should not fail
        assertTrue(true);
    }

    @Test
    public void runScriptString() throws ExecutionException, InterruptedException {
        ScriptingManager.runScript("1+1", "application/test", null).get();

        // should not fail
        assertTrue(true);
    }

    @Test
    public void runScriptUnsupportedMediaType() throws Throwable {
        assertThrows(ScriptingException.class, () -> {
            try {
                ScriptingManager.runScript("1+1", "application/lolcode", null).get();
            }
            catch (InterruptedException | ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void runPrivilegedScript(@TempDir Path folder) throws IOException {
        File file = Paths.get(folder.toString(), "counter.test").toFile();
        Files.writeString(file.toPath(), "1+1");

        ScriptingManager.runPrivilegedScript(file, null);

        // should not fail
        assertTrue(true);
    }

    @Test
    public void runPrivilegedScriptString() throws ExecutionException, InterruptedException {
        ScriptingManager.runPrivilegedScript("1+1", "application/test", null).get();

        // should not fail
        assertTrue(true);
    }

    @Test
    public void runPrivilegedScriptUnsupportedMediaType() throws Throwable {
        assertThrows(ScriptingException.class, () -> {
            try {
                ScriptingManager.runPrivilegedScript("1+1", "application/lolcode", null).get();
            }
            catch (InterruptedException | ExecutionException e) {
                throw e.getCause();
            }
        });
    }

    static class MyScriptingEngine implements ScriptingEngine {
        @Override
        public String getMediaType() {
            return "application/test";
        }

        @Override
        public String getFileExtension() {
            return ".test";
        }

        @Override
        public CompletableFuture<Void> runScript(String script,
                                                 Wot wot,
                                                 ExecutorService executorService) {
            return completedFuture(null);
        }

        @Override
        public CompletableFuture<Void> runPrivilegedScript(String script,
                                                           Wot wot,
                                                           ExecutorService executorService) {
            return completedFuture(null);
        }
    }
}