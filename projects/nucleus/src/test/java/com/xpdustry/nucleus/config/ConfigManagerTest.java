// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.config;

import com.xpdustry.nucleus.update.UpdateChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ConfigManagerTest {

    @TempDir
    private Path directory;

    @Test
    void saves_what_it_reloads() throws Exception {
        final var config = new ConfigManager(this.directory);
        config.save(ConfigPropertyKey.UPDATE_CHANNEL, "SNAPSHOT");
        config.save(ConfigPropertyKey.DATABASE_PORT, "5433");

        final var reloaded = new ConfigManager(this.directory);
        reloaded.onInit();
        assertThat(reloaded.get(ConfigPropertyKey.UPDATE_CHANNEL)).isEqualTo(UpdateChannel.SNAPSHOT);
        assertThat(reloaded.get(ConfigPropertyKey.DATABASE_PORT)).isEqualTo(5433);
        assertThat(Files.readAllLines(this.directory.resolve("config.properties")))
                .containsExactly("nucleus.database.port=5433", "nucleus.update.channel=snapshot");
    }

    @Test
    void keeps_the_file_when_the_value_is_invalid() throws Exception {
        final var config = new ConfigManager(this.directory);
        config.save(ConfigPropertyKey.DATABASE_PORT, "5433");
        assertThatThrownBy(() -> config.save(ConfigPropertyKey.DATABASE_PORT, "port"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(config.get(ConfigPropertyKey.DATABASE_PORT)).isEqualTo(5433);
        assertThat(Files.readAllLines(this.directory.resolve("config.properties")))
                .containsExactly("nucleus.database.port=5433");
    }

    @Test
    void resets_to_the_default() throws Exception {
        final var config = new ConfigManager(this.directory);
        config.save(ConfigPropertyKey.UPDATE_CHANNEL, "release");
        config.reset(ConfigPropertyKey.UPDATE_CHANNEL);
        assertThat(config.isSet(ConfigPropertyKey.UPDATE_CHANNEL)).isFalse();
        assertThat(config.get(ConfigPropertyKey.UPDATE_CHANNEL)).isEqualTo(UpdateChannel.OFF);
        assertThat(Files.readAllLines(this.directory.resolve("config.properties")))
                .isEmpty();
    }
}
