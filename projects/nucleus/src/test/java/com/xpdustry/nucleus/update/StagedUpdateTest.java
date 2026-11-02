// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class StagedUpdateTest {

    @TempDir
    private Path directory;

    private Path mods;
    private Path staging;
    private Path manifest;

    @BeforeEach
    void setup() throws IOException {
        this.mods = Files.createDirectories(this.directory.resolve("mods"));
        this.staging = Files.createDirectories(this.directory.resolve("staging"));
        this.manifest = Files.writeString(this.staging.resolve("manifest.json"), "the manifest");
    }

    @Test
    void replaces_the_files_and_records_the_distribution() throws Exception {
        final var foundation = Files.writeString(this.mods.resolve("foundation.jar"), "old foundation");
        final var added = this.mods.resolve("sql-postgresql.jar");
        final var update = this.staged(foundation, added);

        update.install(this.directory.resolve("previous"), this.directory.resolve("current.json"));

        assertThat(foundation).hasContent("new foundation.jar");
        assertThat(added).hasContent("new sql-postgresql.jar");
        assertThat(this.directory.resolve("previous").resolve("foundation.jar")).hasContent("old foundation");
        assertThat(this.directory.resolve("current.json")).hasContent("the manifest");
    }

    @Test
    void restores_the_files_when_one_fails() throws Exception {
        final var foundation = Files.writeString(this.mods.resolve("foundation.jar"), "old foundation");
        final var added = this.mods.resolve("sql-postgresql.jar");
        final var nucleus = Files.writeString(this.mods.resolve("nucleus.jar"), "old nucleus");
        final var update = this.staged(foundation, added, nucleus);
        Files.delete(update.files().get(nucleus));

        assertThatThrownBy(() ->
                        update.install(this.directory.resolve("previous"), this.directory.resolve("current.json")))
                .isInstanceOf(IOException.class);

        assertThat(foundation).hasContent("old foundation");
        assertThat(nucleus).hasContent("old nucleus");
        assertThat(added).doesNotExist();
        assertThat(this.directory.resolve("current.json")).doesNotExist();
    }

    private UpdateStager.StagedUpdate staged(final Path... targets) throws IOException {
        final var files = new LinkedHashMap<Path, Path>();
        for (final var target : targets) {
            final var name = target.getFileName().toString();
            files.put(target, Files.writeString(this.staging.resolve(name), "new " + name));
        }
        final var manifest =
                TestDistribution.of("2026.10.1", "b", Instant.EPOCH).manifest(URI.create("http://localhost"));
        return new UpdateStager.StagedUpdate(manifest, this.manifest, files);
    }
}
