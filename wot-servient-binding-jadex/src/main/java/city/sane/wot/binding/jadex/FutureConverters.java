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
package city.sane.wot.binding.jadex;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.concurrent.CompletableFuture;

/**
 * Helper class for translating between jadex futures ({@link IFuture}) and java futures ({@link
 * CompletableFuture}).
 */
class FutureConverters {
    private FutureConverters() {
    }

    public static <T> CompletableFuture<T> fromJadex(IFuture<T> jadexFuture) {
        CompletableFuture<T> future = new CompletableFuture<>();
        jadexFuture.addResultListener(new IResultListener<>() {
            @Override
            public void resultAvailable(T t) {
                future.complete(t);
            }

            @Override
            public void exceptionOccurred(Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static <T> IFuture<T> toJadex(CompletableFuture<T> future) {
        Future<T> jadexFuture = new Future<>();
        future.whenComplete((r, e) -> {
            if (e == null) {
                jadexFuture.setResult(r);
            }
            else {
                if (e instanceof Exception) {
                    jadexFuture.setException((Exception) e);
                }
                else {
                    jadexFuture.setException(new Exception(e));
                }
            }
        });

        return jadexFuture;
    }
}
