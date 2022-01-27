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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RefCountResourceTest {
    private Supplier resourceSupplier;
    private Consumer resourceCleanup;

    @BeforeEach
    public void setUp() {
        resourceSupplier = mock(Supplier.class);
        resourceCleanup = mock(Consumer.class);
    }

    @Test
    public void retainShouldCreateCreateResourceOnFirstAccess() throws Throwable {
        AtomicInteger refCount = new AtomicInteger(0);
        RefCountResource resource = new RefCountResource(resourceSupplier, resourceCleanup, refCount, new AtomicReference(null));
        resource.retain();

        verify(resourceSupplier).get();
        assertEquals(1, refCount.get());
    }

    @Test
    public void retainShouldNotCreateResourceOnSecondCall() throws Throwable {
        AtomicInteger refCount = new AtomicInteger(1);
        RefCountResource resource = new RefCountResource(resourceSupplier, resourceCleanup, refCount, new AtomicReference(new Object()));
        resource.retain();

        verify(resourceSupplier, times(0)).get();
        assertEquals(2, refCount.get());
    }

    @Test
    public void releaseShouldCleanupResourceWhenItIsNoLongerUsed() throws Throwable {
        AtomicInteger refCount = new AtomicInteger(1);
        RefCountResource resource = new RefCountResource(resourceSupplier, resourceCleanup, refCount, new AtomicReference(new Object()));
        resource.release();

        verify(resourceCleanup).accept(any());
        assertEquals(0, refCount.get());
    }

    @Test
    public void releaseShouldNotCleanupResourceWhenItIsStillUsed() throws Throwable {
        AtomicInteger refCount = new AtomicInteger(2);
        RefCountResource resource = new RefCountResource(resourceSupplier, resourceCleanup, refCount, new AtomicReference(new Object()));
        resource.release();

        verify(resourceCleanup, times(0)).accept(any());
        assertEquals(1, refCount.get());
    }

    @Test
    public void releaseShouldNotCleanupResourceNonCreatedResource() throws Throwable {
        AtomicInteger refCount = new AtomicInteger(0);
        RefCountResource resource = new RefCountResource(resourceSupplier, resourceCleanup, refCount, new AtomicReference(null));
        resource.release();

        verify(resourceCleanup, times(0)).accept(any());
        assertEquals(0, refCount.get());
    }
}