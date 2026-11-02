// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.config;

import arc.util.CommandHandler;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.nucleus.dependency.Inject;
import java.io.IOException;
import java.util.Optional;

/// The `nconf` server command, to read and change the nucleus config.
public final class ConfigCommand implements PluginListener {

    private final ConfigManager config;
    private final PluginLogger logger;

    @Inject
    public ConfigCommand(final ConfigManager config, final PluginLogger logger) {
        this.config = config;
        this.logger = logger;
    }

    @Override
    public void onServerCommandsRegistration(final CommandHandler handler) {
        handler.register(
                "nconf",
                "<list|get|set|reset> [key] [value...]",
                "Read and change the nucleus config, applied on restart.",
                this::execute);
    }

    private void execute(final String[] args) {
        switch (args[0]) {
            case "list" -> ConfigPropertyKey.all().values().forEach(key -> this.logger.info(this.describe(key)));
            case "get" -> this.key(args).ifPresent(key -> this.logger.info(this.describe(key)));
            case "set" ->
                this.key(args).ifPresent(key -> {
                    if (args.length < 3) {
                        this.logger.error("Missing the value to set {} to", key);
                        return;
                    }
                    try {
                        this.config.save(key, args[2]);
                        this.logger.info("Set {}, restart the server to apply it", this.describe(key));
                    } catch (final IllegalArgumentException e) {
                        this.logger.error("Invalid value for {}: {}", key, String.valueOf(e.getMessage()));
                    } catch (final IOException e) {
                        this.logger.error("Failed to save the config", e);
                    }
                });
            case "reset" ->
                this.key(args).ifPresent(key -> {
                    try {
                        this.config.reset(key);
                        this.logger.info("Reset {}, restart the server to apply it", this.describe(key));
                    } catch (final IOException e) {
                        this.logger.error("Failed to save the config", e);
                    }
                });
            default -> this.logger.error("Unknown action {}, expected list, get, set or reset", args[0]);
        }
    }

    private Optional<ConfigPropertyKey<?>> key(final String[] args) {
        if (args.length < 2) {
            this.logger.error("Missing the key, see them with nconf list");
            return Optional.empty();
        }
        final var key = ConfigPropertyKey.all().get(args[1]);
        if (key == null) {
            this.logger.error("Unknown key {}, see them with nconf list", args[1]);
        }
        return Optional.ofNullable(key);
    }

    private String describe(final ConfigPropertyKey<?> key) {
        final var value = ConfigManager.format(this.config, key);
        final var shown = key.secret() && !value.isEmpty() ? "<hidden>" : value;
        return key + " = " + shown + (this.config.isSet(key) ? "" : " (default)");
    }
}
