package com.github.deroq1337.perms.bukkit.data.utils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ListenableFutureConverter {

    public static @NotNull <V> CompletableFuture<V> toCompletableFuture(@NotNull ListenableFuture<V> listenableFuture) {
        CompletableFuture<V> transformedFuture = new CompletableFuture<>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };

        Futures.addCallback(listenableFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(V result) {
                transformedFuture.complete(result);
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                transformedFuture.completeExceptionally(t);
            }
        }, MoreExecutors.directExecutor());
        return transformedFuture;
    }
}
