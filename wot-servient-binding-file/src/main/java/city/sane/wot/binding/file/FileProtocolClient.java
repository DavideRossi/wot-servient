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
package city.sane.wot.binding.file;

import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.ProtocolClientException;
import city.sane.wot.content.Content;
import city.sane.wot.thing.form.Form;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Allows consuming Things via local files.
 */
public class FileProtocolClient implements ProtocolClient {
    private static final Logger log = LoggerFactory.getLogger(FileProtocolClient.class);
    private static final Map<String, String> EXTENSION_TO_CONTENT_TYPE = Map.of(
            ".json", "application/json",
            ".jsonld", "application/ld+json"
    );
    private final Function<String, Path> hrefToPath;
    // counts active subscriptions. Is used by integration tests to avoid race conditions
    private final AtomicInteger subscriptionsCount = new AtomicInteger(0);

    public FileProtocolClient() {
        this(FileProtocolClient::hrefToPath);
    }

    FileProtocolClient(Function<String, Path> hrefToPath) {
        this.hrefToPath = hrefToPath;
    }

    private static Path hrefToPath(String href) {
        return Paths.get(URI.create(href));
    }

    @Override
    public CompletableFuture<Content> readResource(Form form) {
        return supplyAsync(() -> {
            try {
                Path path = hrefToPath.apply(form.getHref());
                return getContentFromPath(path);
            }
            catch (IOException e) {
                throw new CompletionException(new ProtocolClientException("Unable to read file '" + form.getHref() + "': " + e.getMessage()));
            }
        });
    }

    @Override
    public CompletableFuture<Content> writeResource(Form form, Content content) {
        return supplyAsync(() -> {
            try {
                Path path = hrefToPath.apply(form.getHref());

                Files.write(path, content.getBody(), StandardOpenOption.CREATE);

                return Content.EMPTY_CONTENT;
            }
            catch (IOException e) {
                throw new CompletionException(new ProtocolClientException("Unable to write file '" + form.getHref() + "': " + e.getMessage()));
            }
        });
    }

    @Override
    public Observable<Content> observeResource(Form form) {
        Path path = hrefToPath.apply(form.getHref());
        Path directory = path.getParent();

        // We obtain the file system of the Path
        FileSystem fileSystem = directory.getFileSystem();

        return Observable.using(
                fileSystem::newWatchService,
                service -> Observable.<Content>create(source -> {
                    // We watch for modification events
                    directory.register(service, ENTRY_MODIFY, ENTRY_DELETE, ENTRY_CREATE);

                    pollFileChange(path, service, source);

                    source.onComplete();
                }),
                WatchService::close
        )
                .doOnSubscribe(d -> subscriptionsCount.incrementAndGet())
                .doOnDispose(subscriptionsCount::decrementAndGet)
                .subscribeOn(Schedulers.io());
    }

    private void pollFileChange(Path path,
                                WatchService service,
                                @NonNull ObservableEmitter<Content> source) throws InterruptedException, IOException {
        try {
            // Start the infinite polling loop
            WatchKey watchKey;
            // Wait for the next event
            while (!source.isDisposed() && (watchKey = service.take()) != null) {
                handleWatchKey(path, source, watchKey);

                if (!watchKey.reset()) {
                    break;
                }
            }
        }
        catch (InterruptedException e) {
            if (!source.isDisposed()) {
                throw e;
            }
        }
    }

    private void handleWatchKey(Path path,
                                @NonNull ObservableEmitter<Content> source,
                                WatchKey watchKey) throws IOException {
        for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
            // Get the type of the event
            WatchEvent.Kind<?> kind = watchEvent.kind();

            if (kind == ENTRY_MODIFY || kind == ENTRY_DELETE || kind == ENTRY_CREATE) {
                Path watchEventPath = (Path) watchEvent.context();

                // Call this if the right file is involved
                if (path.getFileName().equals(watchEventPath)) {
                    Content content = getContentFromPath(path);
                    source.onNext(content);
                }
            }
        }
    }

    private Content getContentFromPath(Path path) throws IOException {
        String extension = pathToExtension(path);

        log.debug("Found extension '{}'", extension);
        String contentType = extensionToContentType(extension);
        if (Files.exists(path)) {
            byte[] body = Files.readAllBytes(path);
            return new Content(contentType, body);
        }
        else {
            return Content.EMPTY_CONTENT;
        }
    }

    private String pathToExtension(Path path) {
        String pathStr = path.toString();
        if (pathStr.contains(".")) {
            return pathStr.substring(pathStr.lastIndexOf('.'));
        }
        else {
            return "";
        }
    }

    private String extensionToContentType(String extension) {
        String contentType;

        // try java's FileNameMap first
        contentType = URLConnection.guessContentTypeFromName(extension);
        if (contentType == null) {
            // use own mapping as fallback
            contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);
            if (contentType == null) {
                // fallback
                log.warn("FileClient cannot determine media type for extension {}", extension);
                contentType = "application/octet-stream";
            }
        }

        return contentType;
    }
}