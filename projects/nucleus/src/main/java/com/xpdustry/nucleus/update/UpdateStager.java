// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.json.JsonMapper;

/// Finds the files of the latest distribution this server lacks, the ones of the Mindustry server being pinned by the
/// signed manifest.
final class UpdateStager {

    private final JsonMapper json;
    private final int java;
    private @Nullable String lastChecked;

    /// @param java the Java version the server runs
    UpdateStager(final JsonMapper json, final int java) {
        this.json = json;
        this.java = java;
    }

    /// Stages the latest distribution of the channel, if newer than the current one.
    ///
    /// @param current the distribution installed on this server, null if unknown
    /// @throws IncompatibleUpdateException once per distribution this server cannot run
    Optional<StagedUpdate> stage(
            final ArtifactResolver resolver,
            final UpdateChannel channel,
            final @Nullable DistributionManifest current,
            final DistributionInfo installed)
            throws IOException, InterruptedException {
        final var file = resolver.resolve(new ArtifactLocation.Maven(
                DistributionManifest.ARTIFACT,
                new ArtifactLocation.Version.Latest(channel == UpdateChannel.SNAPSHOT),
                null,
                "json"));
        final var latest = DistributionManifest.read(this.json, file);
        if (latest.commit().equals(this.lastChecked)) {
            return Optional.empty();
        }
        if (current != null && !latest.isNewerThan(current)) {
            this.lastChecked = latest.commit();
            return Optional.empty();
        }
        if (latest.java() > this.java) {
            this.lastChecked = latest.commit();
            throw new IncompatibleUpdateException("The update to " + latest + " needs Java " + latest.java()
                    + ", this server runs Java " + this.java);
        }

        final var files = new LinkedHashMap<Path, Path>();
        for (final var plugin : required(latest, installed.plugins().keySet())) {
            final var live = installed
                    .plugins()
                    .getOrDefault(plugin.name(), installed.mods().resolve(plugin.name() + ".jar"));
            if (Files.exists(live) && sha256(live).equals(plugin.sha256())) {
                continue;
            }
            final var jar = resolver.resolve(new ArtifactLocation.Maven(
                    plugin.name(), new ArtifactLocation.Version.Exact(latest.version()), "plugin", "jar"));
            files.put(live, matching(jar, plugin.sha256()));
        }
        final var server = latest.mindustry();
        if (!installed.mindustry().equals(server.version())) {
            final var jar = resolver.resolve(new ArtifactLocation.Remote(server.url(), "server-release.jar"));
            files.put(installed.server(), matching(jar, server.sha256()));
        }

        this.lastChecked = latest.commit();
        return Optional.of(new StagedUpdate(latest, file, files));
    }

    static boolean isInstalled(final DistributionManifest manifest, final DistributionInfo installed)
            throws IOException {
        for (final var entry : installed.plugins().entrySet()) {
            final var plugin = manifest.plugin(entry.getKey());
            if (plugin.isPresent()
                    && !sha256(entry.getValue()).equals(plugin.get().sha256())) {
                return false;
            }
        }
        return installed.mindustry().equals(manifest.mindustry().version());
    }

    private static Path matching(final Path file, final String sha256) throws IOException {
        if (!sha256(file).equals(sha256)) {
            throw new IOException(file.getFileName() + " does not match the distribution, retrying later");
        }
        return file;
    }

    /// The installed plugins of the distribution and their hard dependencies, the soft ones being left to the owner.
    private static List<DistributionManifest.Plugin> required(
            final DistributionManifest manifest, final Collection<String> installed) {
        final var required = new LinkedHashMap<String, DistributionManifest.Plugin>();
        final var pending = new ArrayDeque<>(installed);
        while (!pending.isEmpty()) {
            final var name = pending.pop();
            if (required.containsKey(name)) {
                continue;
            }
            manifest.plugin(name).ifPresent(plugin -> {
                required.put(name, plugin);
                pending.addAll(plugin.dependencies());
            });
        }
        return List.copyOf(required.values());
    }

    static String sha256(final Path file) throws IOException {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Every JVM supports SHA-256", e);
        }
        try (final var input = new DigestInputStream(Files.newInputStream(file), digest)) {
            input.transferTo(OutputStream.nullOutputStream());
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    static void clear(final Path directory) throws IOException {
        if (Files.exists(directory)) {
            try (final var files = Files.walk(directory)) {
                for (final var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
        Files.createDirectories(directory);
    }

    /// A verified distribution, with the staged file replacing each file of the server.
    ///
    /// @param files the staged files by the files they replace, empty if the server already runs the distribution
    record StagedUpdate(DistributionManifest manifest, Path file, Map<Path, Path> files) {

        boolean requiresRestart() {
            return !this.files.isEmpty();
        }

        /// Replaces the files of the server, backed up in the previous directory, then records the distribution as
        /// the current one.
        ///
        /// Renaming over the loaded jars is safe on Linux, the server keeps reading the files it opened.
        void install(final Path previous, final Path current) throws IOException {
            clear(previous);
            for (final var live : this.files.keySet()) {
                if (Files.exists(live)) {
                    Files.copy(live, previous.resolve(live.getFileName()));
                }
            }
            final var replaced = new ArrayList<Path>();
            try {
                for (final var entry : this.files.entrySet()) {
                    Files.move(entry.getValue(), entry.getKey(), StandardCopyOption.ATOMIC_MOVE);
                    replaced.add(entry.getKey());
                }
            } catch (final IOException e) {
                for (final var live : replaced) {
                    final var backup = previous.resolve(live.getFileName());
                    if (Files.exists(backup)) {
                        Files.copy(backup, live, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        Files.delete(live);
                    }
                }
                throw e;
            }
            Files.copy(this.file, current, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
