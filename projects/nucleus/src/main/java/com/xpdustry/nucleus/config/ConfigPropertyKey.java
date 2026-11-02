// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.config;

import com.xpdustry.nucleus.update.UpdateChannel;
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

    public static final ConfigPropertyKey<String> DATABASE_PASSWORD = secret("nucleus.database.password", "postgres");

    public static final ConfigPropertyKey<Boolean> DATABASE_EMBEDDED = bool("nucleus.database.embedded", true);

    public static final ConfigPropertyKey<String> VPN_API_IO_TOKEN = secret("nucleus.vpn_api_io_token", "");

    public static final ConfigPropertyKey<Boolean> METRICS_ENABLED = bool("nucleus.metrics.enabled", false);

    public static final ConfigPropertyKey<String> DISCORD_TOKEN = secret("nucleus.discord.token", "");

    public static final ConfigPropertyKey<UpdateChannel> UPDATE_CHANNEL =
            enumeration("nucleus.update.channel", UpdateChannel.OFF);

    public static final ConfigPropertyKey<URI> UPDATE_REPOSITORY =
            uri("nucleus.update.repository", URI.create("https://maven.xpdustry.com"));

    private final String name;
    private final T def;
    private final Function<String, T> parser;
    private final Function<T, String> formatter;
    private final boolean secret;

    private ConfigPropertyKey(
            final String name,
            final T def,
            final Function<String, T> parser,
            final Function<T, String> formatter,
            final boolean secret) {
        this.name = name;
        this.def = def;
        this.parser = parser;
        this.formatter = formatter;
        this.secret = secret;
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

    public String format(final T value) {
        return this.formatter.apply(value);
    }

    /// Whether the value must not be shown, such as a password.
    public boolean secret() {
        return this.secret;
    }

    @Override
    public String toString() {
        return this.name;
    }

    private static <T> ConfigPropertyKey<T> registering(
            final String name,
            final T def,
            final Function<String, T> parser,
            final Function<T, String> formatter,
            final boolean secret) {
        if (ALL.containsKey(name)) {
            throw new IllegalStateException("Duplicate key " + name);
        }
        final var key = new ConfigPropertyKey<>(name, def, parser, formatter, secret);
        ALL.put(name, key);
        return key;
    }

    private static ConfigPropertyKey<String> string(final String name, final String def) {
        return registering(name, def, Function.identity(), Function.identity(), false);
    }

    private static ConfigPropertyKey<String> secret(final String name, final String def) {
        return registering(name, def, Function.identity(), Function.identity(), true);
    }

    private static ConfigPropertyKey<Integer> integer(final String name, final int def) {
        return registering(name, def, Integer::parseInt, String::valueOf, false);
    }

    private static ConfigPropertyKey<URI> uri(final String name, final URI def) {
        return registering(name, def, URI::create, URI::toString, false);
    }

    private static ConfigPropertyKey<Boolean> bool(final String name, final boolean def) {
        return registering(
                name,
                def,
                value -> switch (value.toLowerCase(Locale.ROOT)) {
                    case "true" -> true;
                    case "false" -> false;
                    default -> throw new IllegalArgumentException(value + " is not a valid boolean");
                },
                String::valueOf,
                false);
    }

    private static <E extends Enum<E>> ConfigPropertyKey<E> enumeration(final String name, final E def) {
        final var type = def.getDeclaringClass();
        return registering(
                name,
                def,
                value -> Enum.valueOf(type, value.toUpperCase(Locale.ROOT)),
                value -> value.name().toLowerCase(Locale.ROOT),
                false);
    }
}
