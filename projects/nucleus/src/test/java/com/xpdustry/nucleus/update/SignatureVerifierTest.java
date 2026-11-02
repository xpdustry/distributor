// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class SignatureVerifierTest {

    @TempDir
    private Path directory;

    @Test
    void accepts_the_pinned_subkey() throws Exception {
        final var file = Files.writeString(this.directory.resolve("plugin.jar"), "a plugin");
        final var signature = TestKeys.sign(file, TestKeys.TRUSTED_SIGNING_KEY);
        assertThat(TestKeys.verifier(TestKeys.TRUSTED_SIGNER).verify(file, signature))
                .isTrue();
    }

    @Test
    void rejects_another_subkey_of_the_certificate() throws Exception {
        final var file = Files.writeString(this.directory.resolve("plugin.jar"), "a plugin");
        final var signature = TestKeys.sign(file, TestKeys.OTHER_SIGNING_KEY);
        assertThat(TestKeys.verifier(TestKeys.TRUSTED_SIGNER).verify(file, signature))
                .isFalse();
    }

    @Test
    void rejects_a_tampered_file() throws Exception {
        final var file = Files.writeString(this.directory.resolve("plugin.jar"), "a plugin");
        final var signature = TestKeys.sign(file, TestKeys.TRUSTED_SIGNING_KEY);
        Files.writeString(file, "a malicious plugin");
        assertThat(TestKeys.verifier(TestKeys.TRUSTED_SIGNER).verify(file, signature))
                .isFalse();
    }

    @Test
    void rejects_a_revoked_subkey() throws Exception {
        // Signed before the subkey was revoked as compromised, which invalidates its past signatures too
        final var file =
                Files.writeString(this.directory.resolve("payload.txt"), TestKeys.resource("revoked-payload.txt"));
        final var signature = Files.writeString(
                this.directory.resolve("payload.txt.asc"), TestKeys.resource("revoked-payload.txt.asc"));
        assertThat(TestKeys.verifier(TestKeys.REVOKED_SIGNER).verify(file, signature))
                .isFalse();
    }

    @Test
    void rejects_a_certificate_other_than_the_pinned_one() {
        assertThatThrownBy(() -> new SignatureVerifier(
                        TestKeys.resource("impostor-certificate.asc"), TestKeys.FINGERPRINT, TestKeys.TRUSTED_SIGNER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trusts_the_bundled_xpdustry_certificate() {
        assertThatCode(SignatureVerifier::xpdustry).doesNotThrowAnyException();
    }
}
