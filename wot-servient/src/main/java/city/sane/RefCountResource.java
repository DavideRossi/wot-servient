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
package city.sane;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * This class provides a reference counted resource. At the first access ({@link #retain()} an
 * internal reference counter is incremented and the resource is created and returned using sss. The
 * resource can be released with {@link #release()}. This decrements the internal reference counter.
 * As soon as the counter is zero, the resource is destroyed using xxx.
 */
public class RefCountResource<T> {
    private static final Logger log = LoggerFactory.getLogger(RefCountResource.class);
    private final Supplier<? extends T> resourceSupplier;
    private final Consumer<? super T> resourceCleanup;
    private final AtomicInteger refCount;
    private AtomicReference<T> resource;

    public RefCountResource(Supplier<? extends T> resourceSupplier,
                            Consumer<? super T> resourceCleanup) {
        this(
                resourceSupplier,
                resourceCleanup,
                new AtomicInteger(0),
                new AtomicReference(null)
        );
    }

    RefCountResource(Supplier<? extends T> resourceSupplier,
                     Consumer<? super T> resourceCleanup,
                     AtomicInteger refCount,
                     AtomicReference<T> resource) {
        this.resourceSupplier = requireNonNull(resourceSupplier);
        this.resourceCleanup = requireNonNull(resourceCleanup);
        this.refCount = requireNonNull(refCount);
        this.resource = requireNonNull(resource);
    }

    /**
     * Increases the reference count by {@code 1}, ensures the resource is created and returns it.
     */
    public T retain() throws RefCountResourceException {
        synchronized (this) {
            if (refCount.getAndIncrement() == 0) {
                try {
                    log.debug("First Reference. Create Resource.");
                    resource.set(resourceSupplier.get());
                }
                catch (Throwable throwable) {
                    throw new RefCountResourceException(throwable);
                }
            }

            return resource.get();
        }
    }

    /**
     * Decreases the reference count by {@code 1} and deallocates the resource if the reference
     * count reaches at {@code 0}.
     */
    public void release() throws RefCountResourceException {
        synchronized (this) {
            if (refCount.updateAndGet(i -> i > 0 ? i - 1 : i) == 0 && resource.get() != null) {
                log.debug("No more References. Cleanup Resource: {}", resource.get());
                try {
                    resourceCleanup.accept(resource.getAndSet(null));
                }
                catch (Throwable throwable) {
                    throw new RefCountResourceException(throwable);
                }
            }
        }
    }
}
