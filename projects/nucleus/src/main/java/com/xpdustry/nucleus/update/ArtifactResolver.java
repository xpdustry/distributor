// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.http.URIBuilder;
import com.xpdustry.nucleus.version.NucleusVersion;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/// Downloads the files of an update into a directory, the ones of the xpdustry repository once verified to be signed
/// by xpdustry.
final class ArtifactResolver {

    private static final Duration TIMEOUT = Duration.ofMinutes(1);

    private final HttpClient http;
    private final URI repository;
    private final SignatureVerifier verifier;
    private final Path directory;

    ArtifactResolver(
            final HttpClient http, final URI repository, final SignatureVerifier verifier, final Path directory) {
        this.http = http;
        this.repository = repository;
        this.verifier = verifier;
        this.directory = directory;
    }

    /// @throws IOException if the file is missing, or not signed by xpdustry
    Path resolve(final ArtifactLocation location) throws IOException, InterruptedException {
        Files.createDirectories(this.directory);
        return switch (location) {
            case ArtifactLocation.Remote remote -> this.download(remote.uri(), remote.name());
            case ArtifactLocation.Maven maven -> {
                final var uri = this.locate(maven).orElseThrow(() -> new IOException("No file published for " + maven));
                final var name = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
                final var file = this.download(uri, name);
                final var signature = this.download(URI.create(uri + ".asc"), name + ".asc");
                final var signed = this.verifier.verify(file, signature);
                Files.delete(signature);
                if (!signed) {
                    Files.delete(file);
                    throw new IOException("The file at " + uri + " is not signed by xpdustry");
                }
                yield file;
            }
        };
    }

    private Optional<URI> locate(final ArtifactLocation.Maven location) throws IOException, InterruptedException {
        final var version = switch (location.version()) {
            case ArtifactLocation.Version.Exact exact -> Optional.of(exact.version());
            case ArtifactLocation.Version.Latest latest -> this.latest(location.artifact(), latest.snapshot());
        };
        if (version.isEmpty()) {
            return Optional.empty();
        }
        final var name = version.get().toString();
        final var snapshot = version.get().snapshot();
        if (!snapshot) {
            return Optional.of(this.file(location, false, name, name));
        }
        // The files of a snapshot are named after the time it was published
        final var metadata = this.fetchMetadata(
                this.artifact(location.artifact(), true).addPathSegment(name).build());
        if (metadata.isEmpty()) {
            return Optional.empty();
        }
        final var classifier = location.classifier() == null ? "" : location.classifier();
        final var nodes = metadata.get().getElementsByTagName("snapshotVersion");
        for (int i = 0; i < nodes.getLength(); i++) {
            final var node = (Element) nodes.item(i);
            if (text(node, "classifier").equals(classifier)
                    && text(node, "extension").equals(location.extension())) {
                return Optional.of(this.file(location, true, name, text(node, "value")));
            }
        }
        return Optional.empty();
    }

    private Optional<NucleusVersion> latest(final String artifact, final boolean snapshots)
            throws IOException, InterruptedException {
        return this.fetchMetadata(this.artifact(artifact, snapshots).build())
                .flatMap(document -> texts(document, "version").stream()
                        .flatMap(version -> parse(version).stream())
                        .filter(version -> version.snapshot() == snapshots)
                        .max(Comparator.naturalOrder()));
    }

    private Path download(final URI uri, final String name) throws IOException, InterruptedException {
        final var target = this.directory.resolve(name);
        final var response = this.http.send(
                HttpRequest.newBuilder(uri).timeout(TIMEOUT).GET().build(), HttpResponse.BodyHandlers.ofFile(target));
        if (response.statusCode() != 200) {
            Files.deleteIfExists(target);
            throw new IOException("Failed to download " + uri + " (status_code=" + response.statusCode() + ")");
        }
        return target;
    }

    private URIBuilder artifact(final String artifact, final boolean snapshots) {
        return new URIBuilder(this.repository)
                .addPathSegment(snapshots ? "snapshots" : "releases")
                .addPathSegment("com")
                .addPathSegment("xpdustry")
                .addPathSegment(artifact);
    }

    private URI file(
            final ArtifactLocation.Maven location, final boolean snapshots, final String version, final String file) {
        final var classifier = location.classifier() == null ? "" : "-" + location.classifier();
        return this.artifact(location.artifact(), snapshots)
                .addPathSegment(version)
                .addPathSegment(location.artifact() + "-" + file + classifier + "." + location.extension())
                .build();
    }

    private Optional<Document> fetchMetadata(final URI directory) throws IOException, InterruptedException {
        final var uri = URI.create(directory + "/maven-metadata.xml");
        final var response = this.http.send(
                HttpRequest.newBuilder(uri).timeout(TIMEOUT).GET().build(), HttpResponse.BodyHandlers.ofInputStream());
        try (final var body = response.body()) {
            return switch (response.statusCode()) {
                case 200 -> Optional.of(parse(body));
                case 404 -> Optional.empty();
                default ->
                    throw new IOException("Failed to fetch " + uri + " (status_code=" + response.statusCode() + ")");
            };
        }
    }

    private static Document parse(final InputStream input) throws IOException {
        try {
            final var factory = DocumentBuilderFactory.newInstance();
            // The metadata never declares a doctype, refusing one rules out the entity expansion attacks
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setExpandEntityReferences(false);
            return factory.newDocumentBuilder().parse(input);
        } catch (final ParserConfigurationException | SAXException e) {
            throw new IOException("Invalid Maven metadata", e);
        }
    }

    private static Optional<NucleusVersion> parse(final String version) {
        try {
            return Optional.of(new NucleusVersion(version));
        } catch (final IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static List<String> texts(final Document document, final String tag) {
        final var nodes = document.getElementsByTagName(tag);
        final var texts = new ArrayList<String>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            texts.add(nodes.item(i).getTextContent().trim());
        }
        return texts;
    }

    private static String text(final Element element, final String tag) {
        final var nodes = element.getElementsByTagName(tag);
        return nodes.getLength() == 0 ? "" : nodes.item(0).getTextContent().trim();
    }
}
