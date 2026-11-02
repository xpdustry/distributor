// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.bouncycastle.bcpg.KeyIdentifier;
import org.bouncycastle.openpgp.PGPSignatureException;
import org.bouncycastle.openpgp.api.OpenPGPCertificate;
import org.bouncycastle.openpgp.api.OpenPGPDetachedSignatureProcessor;
import org.bouncycastle.openpgp.api.OpenPGPKeyReader;
import org.bouncycastle.openpgp.api.OpenPGPSignature;
import org.bouncycastle.util.encoders.Hex;

/// Accepts the files signed by the pinned subkey of the pinned certificate.
public final class SignatureVerifier {

    private static final String XPDUSTRY_FINGERPRINT = "FEDDDE64C064D93A5AB7FB9ECE816250EED518B5";
    private static final String MONOREPO_SIGNER_FINGERPRINT = "D9445182EAFC38CEA25BF08357A09F6F5F7D0D5A";

    private final OpenPGPCertificate certificate;
    private final KeyIdentifier signer;

    SignatureVerifier(final String certificate, final String fingerprint, final String signer) throws IOException {
        this.certificate = new OpenPGPKeyReader().parseCertificate(certificate);
        if (!this.certificate.getKeyIdentifier().matchesExplicit(identifier(fingerprint))) {
            throw new IllegalArgumentException("The certificate is not the one of " + fingerprint);
        }
        this.signer = identifier(signer);
    }

    static SignatureVerifier xpdustry() throws IOException {
        try (final InputStream certificate =
                Objects.requireNonNull(SignatureVerifier.class.getResourceAsStream("KEYS.asc"))) {
            return new SignatureVerifier(
                    new String(certificate.readAllBytes(), StandardCharsets.UTF_8),
                    XPDUSTRY_FINGERPRINT,
                    MONOREPO_SIGNER_FINGERPRINT);
        }
    }

    boolean verify(final Path file, final Path signature) throws IOException {
        try (final var data = Files.newInputStream(file);
                final var signatures = Files.newInputStream(signature)) {
            // The processor drops the signatures of keys outside the certificate, and checks the others
            for (final var candidate : new OpenPGPDetachedSignatureProcessor()
                    .addVerificationCertificate(this.certificate)
                    .addSignatures(signatures)
                    .process(data)) {
                // Any subkey of the certificate yields a valid signature, so the signer must be checked too
                final var issuer = candidate.getIssuer();
                if (issuer != null && issuer.getKeyIdentifier().matchesExplicit(this.signer) && isValid(candidate)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean isValid(final OpenPGPSignature.OpenPGPDocumentSignature signature) {
        try {
            return signature.isValid();
        } catch (final PGPSignatureException e) {
            return false;
        }
    }

    private static KeyIdentifier identifier(final String fingerprint) {
        return new KeyIdentifier(Hex.decode(fingerprint));
    }
}
