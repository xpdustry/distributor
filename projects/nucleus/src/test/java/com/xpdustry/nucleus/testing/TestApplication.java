// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.testing;

import com.google.gson.Gson;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.nucleus.config.ConfigManager;
import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// An application made of the given modules, on top of what the plugin provides outside of Mindustry.
///
/// Its listeners are initialized right away and exit in reverse order on close. Bound instances are left alone, which
/// allows applications to share a dependency, such as a database, owned by another application.
public final class TestApplication implements AutoCloseable {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final List<PluginListener> started = new ArrayList<>();
    private final Set<Object> bound = Collections.newSetFromMap(new IdentityHashMap<>());
    private final DependencyService dependencies;

    private TestApplication(final Path directory, final DependencyModule... modules) {
        final var base = (DependencyModule) binder -> {
            binder.bind(Path.class, directory);
            binder.bind(Gson.class, new Gson());
            binder.bind(Executor.class, this.executor);
            binder.bind(ConfigManager.class, new ConfigManager());
            binder.bind(PluginLogger.class, new TestPluginLogger());
        };
        final var all = new DependencyModule[modules.length + 1];
        all[0] = base;
        System.arraycopy(modules, 0, all, 1, modules.length);
        this.dependencies = DependencyService.create(Arrays.stream(all)
                .<DependencyModule>map(module -> binder -> module.configure(new RecordingBinder(binder)))
                .toArray(DependencyModule[]::new));
    }

    public static TestApplication start(final Path directory, final DependencyModule... modules) {
        final var application = new TestApplication(directory, modules);
        try {
            for (final var dependency : application.dependencies.resolveAll()) {
                if (dependency instanceof PluginListener listener && !application.bound.contains(dependency)) {
                    listener.onInit();
                    application.started.add(listener);
                }
            }
        } catch (final RuntimeException e) {
            application.close();
            throw e;
        }
        return application;
    }

    public <T> T resolve(final Class<T> type) {
        return this.dependencies.resolve(type);
    }

    @Override
    public void close() {
        this.started.reversed().forEach(PluginListener::onExit);
        this.executor.close();
    }

    private final class RecordingBinder implements DependencyService.Binder {

        private final DependencyService.Binder delegate;

        private RecordingBinder(final DependencyService.Binder delegate) {
            this.delegate = delegate;
        }

        @Override
        public <T> void bind(final Class<T> type, final T instance) {
            TestApplication.this.bound.add(instance);
            this.delegate.bind(type, instance);
        }

        @Override
        public <T> void bind(final Class<T> type, final Class<? extends T> impl) {
            this.delegate.bind(type, impl);
        }
    }
}
