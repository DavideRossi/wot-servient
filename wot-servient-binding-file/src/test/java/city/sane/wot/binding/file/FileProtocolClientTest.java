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

import city.sane.wot.content.Content;
import city.sane.wot.thing.form.Form;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.observers.LambdaObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileProtocolClientTest {
    private Form form;
    private Function hrefToPath;
    private Path path;
    private Path directory;
    private FileSystem fileSystem;
    private WatchService watchService;

    @BeforeEach
    public void setUp() {
        form = mock(Form.class);
        hrefToPath = mock(Function.class);
        path = mock(Path.class);
        directory = mock(Path.class);
        fileSystem = mock(FileSystem.class);
        watchService = mock(WatchService.class);
    }

    @Test
    public void subscribeResourceShouldCreateWatchService() throws IOException {
        when(hrefToPath.apply(any())).thenReturn(path);
        when(path.getParent()).thenReturn(directory);
        when(directory.getFileSystem()).thenReturn(fileSystem);
        LambdaObserver<Content> observer = new LambdaObserver<>(n -> {
        }, e -> {
        }, () -> {
        }, s -> {
        });

        FileProtocolClient client = new FileProtocolClient(hrefToPath);
        client.observeResource(form).subscribe(observer);

        verify(directory, timeout(5 * 1000L)).register(any(), any());
    }

    @Test
    public void subscribeResourceShouldCloseWatchServiceWhenObserverIsDone() throws IOException, InterruptedException {
        when(hrefToPath.apply(any())).thenReturn(path);
        when(path.getParent()).thenReturn(directory);
        when(directory.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.newWatchService()).thenReturn(watchService);
        when(watchService.take()).thenAnswer(new AnswersWithDelay(5 * 1000L, new Returns(null)));

        FileProtocolClient client = new FileProtocolClient(hrefToPath);
        Disposable subscribe = client.observeResource(form).subscribe();

        // wait until subscriptions as been established
        verify(directory, timeout(5 * 1000L)).register(any(), any());

        subscribe.dispose();

        verify(watchService, timeout(5 * 1000L)).close();
    }
}