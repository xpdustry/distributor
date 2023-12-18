/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.xpdustry.distributor.core.logging;

import fr.xpdustry.distributor.api.plugin.PluginDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mindustry.mod.ModClassLoader;
import mindustry.mod.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public final class ArcLoggerFactory implements ILoggerFactory {

    private final Map<String, ArcLogger> loggers = new ConcurrentHashMap<>();

    {
        this.loggers.put(Logger.ROOT_LOGGER_NAME, new ArcLogger(Logger.ROOT_LOGGER_NAME, null));
    }

    @Override
    public Logger getLogger(final String name) {
        if (this.loggers.containsKey(name)) {
            return this.loggers.get(name);
        }

        Class<?> caller;
        var cache = true;

        try {
            caller = Class.forName(name);
        } catch (final ClassNotFoundException ignored1) {
            final var candidate = tryFindCaller(Thread.currentThread().getStackTrace());
            if (candidate == null) {
                return new ArcLogger(name, null);
            }
            try {
                caller = Class.forName(candidate);
                cache = false;
            } catch (final ClassNotFoundException ignored2) {
                return new ArcLogger(name, null);
            }
        }

        if (Plugin.class.isAssignableFrom(caller)) {
            @SuppressWarnings("unchecked")
            final var descriptor = PluginDescriptor.from((Class<? extends Plugin>) caller);
            // Plugin loggers are found on the first lookup, thus if the cache flag is false,
            // it means a custom logger has been created inside the plugin class
            final var logger = cache
                    ? new ArcLogger(descriptor.getDisplayName(), null)
                    : new ArcLogger(name, descriptor.getDisplayName());
            if (cache) {
                this.loggers.put(name, logger);
            }
            return logger;
        }

        ClassLoader classLoader = caller.getClassLoader();
        while (classLoader != null) {
            if (classLoader.getParent() instanceof ModClassLoader) {
                final PluginDescriptor descriptor;
                try {
                    descriptor = PluginDescriptor.from(classLoader);
                } catch (final IOException ignored) {
                    return this.createLogger(name, null, cache);
                }
                return this.createLogger(name, descriptor.getDisplayName(), cache);
            }
            classLoader = classLoader.getParent();
        }

        return this.createLogger(name, null, cache);
    }

    private ArcLogger createLogger(final String name, final @Nullable String plugin, final boolean cache) {
        final var logger = new ArcLogger(name, plugin);
        if (cache) {
            this.loggers.put(name, logger);
        }
        return logger;
    }

    private @Nullable String tryFindCaller(final StackTraceElement[] stacktrace) {
        return Arrays.stream(stacktrace)
                .skip(3) // 0: stacktrace call, 1: ArcLoggerFactory#getLogger, 2: LoggerFactory#getLogger
                .map(StackTraceElement::getClassName)
                // Skips the logger wrappers
                .dropWhile(clazz -> clazz.startsWith("org.slf4j")
                        || clazz.startsWith("java.util.logging")
                        || clazz.startsWith("sun.util.logging"))
                .findFirst()
                .orElse(null);
    }
}
