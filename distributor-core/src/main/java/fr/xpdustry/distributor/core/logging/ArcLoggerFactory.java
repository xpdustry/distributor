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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mindustry.mod.ModClassLoader;
import mindustry.mod.Plugin;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public final class ArcLoggerFactory implements ILoggerFactory {

    private final Map<String, ArcLogger> cache = new ConcurrentHashMap<>();

    @Override
    public Logger getLogger(final String name) {
        if (this.cache.containsKey(name)) {
            return this.cache.get(name);
        }

        final Class<?> caller;
        try {
            caller = Class.forName(name);
        } catch (final ClassNotFoundException ignored) {
            return this.cache.computeIfAbsent(name, key -> new ArcLogger(name, null));
        }

        if (Plugin.class.isAssignableFrom(caller)) {
            @SuppressWarnings("unchecked")
            final var plugin = (Class<? extends Plugin>) caller;
            return this.cache.computeIfAbsent(
                    name, key -> new ArcLogger(PluginDescriptor.from(plugin).getDisplayName(), null));
        }

        ClassLoader classLoader = caller.getClassLoader();
        while (classLoader.getParent() != null && !(classLoader.getParent() instanceof ModClassLoader)) {
            classLoader = classLoader.getParent();
        }

        if (classLoader.getParent() instanceof ModClassLoader) {
            final PluginDescriptor descriptor;
            try {
                descriptor = PluginDescriptor.from(classLoader);
            } catch (final Exception ignored) {
                return this.cache.computeIfAbsent(name, key -> new ArcLogger(caller.getName(), null));
            }
            return this.cache.computeIfAbsent(
                    name, key -> new ArcLogger(caller.getName(), descriptor.getDisplayName()));
        }

        return this.cache.computeIfAbsent(name, key -> new ArcLogger(caller.getName(), null));
    }
}
