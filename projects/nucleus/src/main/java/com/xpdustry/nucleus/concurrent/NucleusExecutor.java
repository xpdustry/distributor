// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.concurrent;

import com.uber.nullaway.annotations.Initializer;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.nucleus.dependency.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("NotNullFieldNotInitialized")
final class NucleusExecutor implements Executor, PluginListener {

    private final PluginLogger logger;
    private ExecutorService executor;

    @Inject
    public NucleusExecutor(final PluginLogger logger) {
        this.logger = logger;
    }

    @Initializer
    @Override
    public void onInit() {
        this.executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
                .name("nucleus-worker")
                .uncaughtExceptionHandler((thread, exception) -> this.logger.error(
                        "An uncaught error occurred in the async task thread {}", thread.getName(), exception))
                .factory());
    }

    @Override
    public void onExit() {
        this.executor.close();
    }

    @Override
    public void execute(final Runnable command) {
        this.executor.execute(command);
    }
}
