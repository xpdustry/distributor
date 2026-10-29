// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.config;

import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.nucleus.dependency.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.jspecify.annotations.Nullable;

public final class ConfigManager implements PluginListener {

    private final Map<String, Object> entries = new HashMap<>();
    private final @Nullable Path file;

    @Inject
    public ConfigManager(final Path directory) {
        this.file = directory.resolve("config.properties");
    }

    public ConfigManager() {
        this.file = null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final ConfigPropertyKey<T> key) {
        final var value = (T) this.entries.get(key.name());
        return value == null ? key.def() : value;
    }

    public <T> ConfigManager set(final ConfigPropertyKey<T> key, final T value) {
        this.entries.put(key.name(), value);
        return this;
    }

    @Override
    public void onInit() {
        if (this.file == null || Files.notExists(this.file)) {
            return;
        }

        final var properties = new Properties();
        try (final var reader = Files.newBufferedReader(this.file)) {
            properties.load(reader);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read the config file", e);
        }

        for (final var name : properties.stringPropertyNames()) {
            final var key = ConfigPropertyKey.all().get(name);
            if (key == null) {
                throw new IllegalStateException("The key " + name + " is unused");
            }
            try {
                this.entries.put(name, key.parse(properties.getProperty(name)));
            } catch (final Exception e) {
                throw new IllegalArgumentException("Failed to parse " + name, e);
            }
        }
    }
}
