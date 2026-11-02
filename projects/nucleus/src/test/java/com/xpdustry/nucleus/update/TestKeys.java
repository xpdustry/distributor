// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.api.OpenPGPDetachedSignatureGenerator;
import org.bouncycastle.openpgp.api.OpenPGPKeyReader;

/// The throwaway keys of the tests, made with gpg: a certificate with a trusted, another and a revoked signing subkey.
final class TestKeys {

    static final String FINGERPRINT = "353BFB2CB1BC1C7E8D2E9A602AC104437A28A8E0";
    static final String TRUSTED_SIGNER = "69DD175162420D3DA5F98578ACB90962AC50C7FF";
    static final String REVOKED_SIGNER = "C70698FA7A085362599D30B79F65758E3C842765";
    static final String TRUSTED_SIGNING_KEY = "trusted-signing-key.asc";
    static final String OTHER_SIGNING_KEY = "other-signing-key.asc";

    private TestKeys() {}

    static String resource(final String name) throws IOException {
        try (final InputStream input = Objects.requireNonNull(TestKeys.class.getResourceAsStream(name))) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static SignatureVerifier verifier(final String signer) throws IOException {
        return new SignatureVerifier(resource("certificate.asc"), FINGERPRINT, signer);
    }

    /// Signs the file with the given key, next to it.
    static Path sign(final Path file, final String key) throws IOException, PGPException {
        final var signer =
                new OpenPGPDetachedSignatureGenerator().addSigningKey(new OpenPGPKeyReader().parseKey(resource(key)));
        try (final var input = Files.newInputStream(file)) {
            final var signature = file.resolveSibling(file.getFileName() + ".asc");
            Files.writeString(signature, signer.sign(input).getFirst().toAsciiArmoredString());
            return signature;
        }
    }
}
