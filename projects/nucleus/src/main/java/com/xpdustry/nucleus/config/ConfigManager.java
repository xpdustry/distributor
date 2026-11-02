// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.config;

import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.nucleus.dependency.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;

public final class ConfigManager implements PluginListener {

    private final Map<String, Object> entries = new ConcurrentHashMap<>();
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

    /// Returns whether the key has a value other than its default.
    public boolean isSet(final ConfigPropertyKey<?> key) {
        return this.entries.containsKey(key.name());
    }

    /// Parses the value of the key and saves it to the config file.
    ///
    /// @throws IllegalArgumentException if the value is invalid for the key
    public <T> void save(final ConfigPropertyKey<T> key, final String value) throws IOException {
        this.entries.put(key.name(), key.parse(value));
        this.write();
    }

    /// Resets the key to its default value and saves the config file.
    public void reset(final ConfigPropertyKey<?> key) throws IOException {
        this.entries.remove(key.name());
        this.write();
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

    private void write() throws IOException {
        if (this.file == null) {
            return;
        }
        final var properties = new Properties();
        for (final var key : ConfigPropertyKey.all().values()) {
            if (this.isSet(key)) {
                properties.setProperty(key.name(), format(this, key));
            }
        }
        final var writer = new StringWriter();
        properties.store(writer, null);
        // Drops the date comment and sorts the entries, which store writes in no particular order
        final var lines = writer.toString()
                .lines()
                .filter(line -> !line.startsWith("#"))
                .sorted()
                .toList();
        Files.write(this.file, lines);
    }

    static <T> String format(final ConfigManager config, final ConfigPropertyKey<T> key) {
        return key.format(config.get(key));
    }
}
