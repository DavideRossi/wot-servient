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
package city.sane.wot.thing.property;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * This class represented the container for the read and write handlers of a {@link ThingProperty}.
 * The handlers are executed when the property is read or written.
 */
public class PropertyState<T> {
    private final Subject<Optional<T>> subject;
    private T value;
    private Supplier<CompletableFuture<T>> readHandler;
    private Function<T, CompletableFuture<T>> writeHandler;

    public PropertyState() {
        this(PublishSubject.create(), null, null, null);
    }

    PropertyState(Subject<Optional<T>> subject,
                  T value,
                  Supplier<CompletableFuture<T>> readHandler,
                  Function<T, CompletableFuture<T>> writeHandler) {
        this.subject = requireNonNull(subject);
        this.value = value;
        this.readHandler = readHandler;
        this.writeHandler = writeHandler;
    }

    public Subject<Optional<T>> getSubject() {
        return subject;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Supplier<CompletableFuture<T>> getReadHandler() {
        return readHandler;
    }

    public void setReadHandler(Supplier<CompletableFuture<T>> readHandler) {
        this.readHandler = readHandler;
    }

    public Function<T, CompletableFuture<T>> getWriteHandler() {
        return writeHandler;
    }

    public void setWriteHandler(Function<T, CompletableFuture<T>> writeHandler) {
        this.writeHandler = writeHandler;
    }
}
