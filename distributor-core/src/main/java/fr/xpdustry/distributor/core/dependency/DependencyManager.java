/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.core.dependency;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Simpler LibraryManager from https://github.com/Byteflux/libby, under the MIT license.
public final class DependencyManager {

    private static final Logger logger = LoggerFactory.getLogger(DependencyManager.class);

    private final List<String> repositories = new ArrayList<>();
    private final Map<Set<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    private final Path directory;

    public DependencyManager(final Path directory) {
        this.directory = directory;
    }

    public void addRepository(final String url) {
        final var repo = url.endsWith("/") ? url : url + '/';
        synchronized (this.repositories) {
            this.repositories.add(repo);
        }
    }

    public void addMavenLocal() {
        this.addRepository(Paths.get(System.getProperty("user.home"))
                .resolve(".m2/repository")
                .toUri()
                .toString());
    }

    public void addMavenCentral() {
        this.addRepository("https://repo1.maven.org/maven2/");
    }

    public void addSonatype() {
        this.addRepository("https://oss.sonatype.org/content/groups/public/");
    }

    public void addXpdustryMaven() {
        this.addRepository("https://maven.xpdustry.fr/releases/");
    }

    public void addJitPack() {
        this.addRepository("https://jitpack.io/");
    }

    public Collection<String> resolve(final Dependency dependency) {
        final List<String> urls = new ArrayList<>();
        for (final var repository : this.repositories) {
            urls.add(repository + dependency.path());
        }
        return Collections.unmodifiableList(urls);
    }

    public IsolatedClassLoader createClassLoaderFor(final Dependency... dependencies) {
        return this.loaders.computeIfAbsent(Set.of(dependencies), key -> {
            final var urls = new ArrayList<URL>();
            for (final var dependency : key) {
                try {
                    urls.add(this.downloadDependency(dependency).toUri().toURL());
                } catch (final MalformedURLException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return new IsolatedClassLoader(urls.toArray(new URL[0]));
        });
    }

    public Path downloadDependency(final Dependency dependency) {
        final Path file = this.directory.resolve(dependency.path());
        if (Files.exists(file)) {
            return file;
        }

        final List<String> urls = new ArrayList<>();
        for (final var repository : this.repositories) {
            urls.add(repository + dependency.path());
        }

        if (urls.isEmpty()) {
            throw new RuntimeException("Dependency '" + dependency + "' couldn't be resolved, add a repository");
        }

        MessageDigest md = null;
        if (dependency.checksum().length != 0) {
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (final NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        final Path out = file.resolveSibling(file.getFileName() + ".tmp");
        out.toFile().deleteOnExit();

        try {
            Files.createDirectories(file.getParent());

            for (final String url : urls) {
                final byte[] bytes = this.downloadDependency(url);
                if (bytes == null) {
                    continue;
                }

                if (md != null) {
                    final byte[] checksum = md.digest(bytes);
                    if (!Arrays.equals(checksum, dependency.checksum())) {
                        logger.error("*** INVALID CHECKSUM ***");
                        logger.error(" Dependency :  " + dependency);
                        logger.error(" URL :  " + url);
                        logger.error(" Expected :  " + Base64.getEncoder().encodeToString(dependency.checksum()));
                        logger.error(" Actual :  " + Base64.getEncoder().encodeToString(checksum));
                        logger.error(" Expected :  " + new String(dependency.checksum(), StandardCharsets.UTF_8));
                        logger.error(" Actual :  " + new String(checksum, StandardCharsets.UTF_8));
                        throw new RuntimeException("Invalid checksum for dependency '" + dependency + "'");
                    }
                }

                Files.write(out, bytes);
                Files.move(out, file);

                return file;
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            try {
                Files.deleteIfExists(out);
            } catch (final IOException ignored) {
            }
        }

        throw new RuntimeException("Failed to download library '" + dependency + "'");
    }

    private byte @Nullable [] downloadDependency(final String url) {
        try {
            logger.info("Downloading dependency from " + url);
            final var connection = new URL(url).openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (final InputStream in = connection.getInputStream()) {
                int len;
                final byte[] buf = new byte[8192];
                final var out = new ByteArrayOutputStream();

                try {
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                } catch (final SocketTimeoutException e) {
                    logger.warn("Download timed out: " + connection.getURL());
                    return null;
                }

                logger.info("Downloaded library " + connection.getURL());
                return out.toByteArray();
            }
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException(e);
        } catch (final IOException e) {
            if (e instanceof FileNotFoundException) {
                logger.debug("File not found: " + url);
            } else if (e instanceof SocketTimeoutException) {
                logger.debug("Connect timed out: " + url);
            } else if (e instanceof UnknownHostException) {
                logger.debug("Unknown host: " + url);
            } else {
                logger.debug("Unexpected IOException", e);
            }

            return null;
        }
    }
}
