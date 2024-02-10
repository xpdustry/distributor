/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.logger.simple;

import arc.util.serialization.Json;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mindustry.mod.ModClassLoader;
import mindustry.mod.Mods;
import mindustry.mod.Plugin;
import org.jspecify.annotations.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public final class DistributorLoggerFactory implements ILoggerFactory {

    private static final List<String> LOGGING_PACKAGES = List.of("org.slf4j", "java.util.logging", "sun.util.logging");

    private final Map<String, DistributorLogger> loggers = new ConcurrentHashMap<>();

    {
        this.loggers.put(Logger.ROOT_LOGGER_NAME, new DistributorLogger(Logger.ROOT_LOGGER_NAME, null));
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
                return new DistributorLogger(name, null);
            }
            try {
                caller = Class.forName(candidate);
                cache = false;
            } catch (final ClassNotFoundException ignored2) {
                return new DistributorLogger(name, null);
            }
        }

        if (Plugin.class.isAssignableFrom(caller)) {
            final var display = getPluginDisplayName(caller.getClassLoader());
            if (display == null) {
                return new DistributorLogger(name, null);
            }
            // Plugin loggers are found on the first lookup, thus if the cache flag is false,
            // it means a custom logger has been created inside the plugin class
            final var logger = cache ? new DistributorLogger(display, null) : new DistributorLogger(name, display);
            if (cache) {
                this.loggers.put(name, logger);
            }
            return logger;
        }

        ClassLoader loader = caller.getClassLoader();
        String display = null;
        while (loader != null) {
            if (loader.getParent() instanceof ModClassLoader) {
                display = getPluginDisplayName(loader);
                break;
            }
            loader = loader.getParent();
        }

        final var logger = new DistributorLogger(name, display);
        if (cache) {
            this.loggers.put(name, logger);
        }
        return logger;
    }

    private @Nullable String tryFindCaller(final StackTraceElement[] stacktrace) {
        return Arrays.stream(stacktrace)
                .skip(3) // 0: stacktrace call, 1: DistributorLoggerFactory#getLogger, 2: LoggerFactory#getLogger
                .map(StackTraceElement::getClassName)
                // Skips the logger wrappers
                .dropWhile(clazz -> LOGGING_PACKAGES.stream().anyMatch(clazz::startsWith))
                .findFirst()
                .orElse(null);
    }

    private @Nullable String getPluginDisplayName(final ClassLoader loader) {
        var resource = loader.getResourceAsStream("plugin.json");
        if (resource == null) {
            resource = loader.getResourceAsStream("plugin.hjson");
            if (resource == null) {
                return null;
            }
        }
        try (final var input = resource) {
            final var meta = new Json().fromJson(Mods.ModMeta.class, input);
            meta.cleanup();
            return meta.displayName();
        } catch (final Exception e) {
            return null;
        }
    }
}
