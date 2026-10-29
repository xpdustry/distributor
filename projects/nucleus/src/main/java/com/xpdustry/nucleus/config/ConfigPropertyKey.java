// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.config;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class ConfigPropertyKey<T> {

    private static final Map<String, ConfigPropertyKey<?>> ALL = new LinkedHashMap<>();

    public static final ConfigPropertyKey<String> SERVER_NAME = string("nucleus.server.name", "unknown");

    public static final ConfigPropertyKey<URI> SERVER_DISCORD =
            uri("nucleus.server.discord", URI.create("https://discord.xpdustry.com"));

    public static final ConfigPropertyKey<String> DATABASE_HOST = string("nucleus.database.host", "localhost");

    public static final ConfigPropertyKey<Integer> DATABASE_PORT = integer("nucleus.database.port", 5432);

    public static final ConfigPropertyKey<String> DATABASE_NAME = string("nucleus.database.name", "postgres");

    public static final ConfigPropertyKey<String> DATABASE_USERNAME = string("nucleus.database.username", "postgres");

    public static final ConfigPropertyKey<String> DATABASE_PASSWORD = string("nucleus.database.password", "postgres");

    public static final ConfigPropertyKey<Boolean> DATABASE_EMBEDDED = bool("nucleus.database.embedded", true);

    public static final ConfigPropertyKey<String> VPN_API_IO_TOKEN = string("nucleus.vpn_api_io_token", "");

    public static final ConfigPropertyKey<Boolean> METRICS_ENABLED = bool("nucleus.metrics.enabled", false);

    public static final ConfigPropertyKey<String> DISCORD_TOKEN = string("nucleus.discord.token", "");

    private final String name;
    private final T def;
    private final Function<String, T> parser;

    private ConfigPropertyKey(final String name, final T def, final Function<String, T> parser) {
        this.name = name;
        this.def = def;
        this.parser = parser;
    }

    public static Map<String, ConfigPropertyKey<?>> all() {
        return Collections.unmodifiableMap(ALL);
    }

    public String name() {
        return this.name;
    }

    public T def() {
        return this.def;
    }

    public T parse(final String value) {
        return this.parser.apply(value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    private static <T> ConfigPropertyKey<T> registering(
            final String name, final T def, final Function<String, T> parser) {
        if (ALL.containsKey(name)) {
            throw new IllegalStateException("Duplicate key " + name);
        }
        final var key = new ConfigPropertyKey<>(name, def, parser);
        ALL.put(name, key);
        return key;
    }

    private static ConfigPropertyKey<String> string(final String name, final String def) {
        return registering(name, def, Function.identity());
    }

    private static ConfigPropertyKey<Integer> integer(final String name, final int def) {
        return registering(name, def, Integer::parseInt);
    }

    private static ConfigPropertyKey<URI> uri(final String name, final URI def) {
        return registering(name, def, URI::create);
    }

    private static ConfigPropertyKey<Boolean> bool(final String name, final boolean def) {
        return registering(name, def, value -> switch (value.toLowerCase(Locale.ROOT)) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalArgumentException(value + " is not a valid boolean");
        });
    }
}
