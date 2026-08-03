// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

import java.util.Objects;
import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

public final class MindustryThread {

    private static @Nullable Thread thread = null;

    @API(status = API.Status.EXPERIMENTAL)
    public static void checkIsMainThread(final String method) {
        if (!isMainThread()) {
            throw new IllegalStateException(method + " must be invoked from the main thread");
        }
    }

    public static boolean isMainThread() {
        return Objects.requireNonNull(thread, "The main thread has not been captured yet")
                .equals(Thread.currentThread());
    }

    @API(status = API.Status.INTERNAL, consumers = "com.xpdustry.foundation.FoundationPlugin")
    public static void captureMainThread() {
        if (thread != null) {
            throw new IllegalStateException("Already captured the main thread");
        }
        thread = Thread.currentThread();
    }

    private MindustryThread() {}
}
