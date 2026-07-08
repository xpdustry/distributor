// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.player;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.zip.CRC32;
import mindustry.gen.Player;
import mindustry.net.Administration;

/// The Mindustry identity format: a combination of a UUID and a USID.
public sealed interface MUUID permits MUUIDImpl {

    /// Creates an [MUUID] from a UUID and USID.
    ///
    /// @param uuid the UUID
    /// @param usid the USID
    /// @return the MUUID
    /// @throws IllegalArgumentException if the UUID or USID is invalid
    static MUUID of(final String uuid, final String usid) {
        checkUuid(uuid);
        checkUsid(usid);
        return new MUUIDImpl(uuid, usid);
    }

    /// Creates an [MUUID] from numeric UUID and USID values.
    ///
    /// @param uuid the UUID value
    /// @param usid the USID value
    /// @return the MUUID
    static MUUID of(final long uuid, final long usid) {
        final var uuidBuffer = ByteBuffer.allocate(16);
        uuidBuffer.putLong(uuid);

        final var crc32 = new CRC32();
        crc32.update(uuidBuffer.array(), 0, 8);
        uuidBuffer.putLong(crc32.getValue());

        final var usidBuffer = ByteBuffer.allocate(8);
        usidBuffer.putLong(usid);

        return new MUUIDImpl(
                Base64.getEncoder().encodeToString(uuidBuffer.array()),
                Base64.getEncoder().encodeToString(usidBuffer.array()));
    }

    /// Creates an [MUUID] from a [Player].
    ///
    /// @param player the player
    /// @return the MUUID
    static MUUID from(final Player player) {
        return of(player.uuid(), player.usid());
    }

    /// Creates an [MUUID] from [Administration.PlayerInfo].
    ///
    /// @param info the player info
    /// @return the MUUID
    static MUUID from(final Administration.PlayerInfo info) {
        return of(info.id, info.adminUsid);
    }

    /// Returns whether the given string is a valid Mindustry UUID.
    ///
    /// @param uuid the UUID to check
    /// @return `true` if the given string is a valid UUID, `false` otherwise
    static boolean isUuid(final String uuid) {
        if (uuid.length() % 4 != 0) return false;
        try {
            final var bytes = Base64.getDecoder().decode(uuid);
            if (bytes.length != 16) return false;
            final var crc32 = new CRC32();
            crc32.update(bytes, 0, 8);
            return crc32.getValue() == ByteBuffer.wrap(bytes, 8, 8).getLong();
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /// Throws if the given string is not a valid Mindustry UUID.
    ///
    /// @param uuid the UUID to check
    /// @throws IllegalArgumentException if the UUID is invalid
    static void checkUuid(final String uuid) {
        if (!isUuid(uuid)) {
            throw new IllegalArgumentException(String.format("Invalid UUID: %s", uuid));
        }
    }

    /// Returns whether the given string is a valid USID.
    ///
    /// @param usid the USID to check
    /// @return `true` if the given string is a valid USID, `false` otherwise
    static boolean isUsid(final String usid) {
        if (usid.length() % 4 != 0) return false;
        try {
            final var bytes = Base64.getDecoder().decode(usid);
            return bytes.length == 8;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /// Throws if the given string is not a valid USID.
    ///
    /// @param usid the USID to check
    /// @throws IllegalArgumentException if the USID is invalid
    static void checkUsid(final String usid) {
        if (!isUsid(usid)) {
            throw new IllegalArgumentException(String.format("Invalid USID: %s", usid));
        }
    }

    /// Returns the encoded Mindustry UUID.
    String uuid();

    /// Returns the UUID as decoded bytes.
    default byte[] uuidAsBytes() {
        return Base64.getDecoder().decode(this.uuid());
    }

    /// Returns the UUID as a long, without the CRC32 checksum.
    default long uuidAsLong() {
        return ByteBuffer.wrap(this.uuidAsBytes()).getLong();
    }

    /// Returns the encoded USID.
    String usid();

    /// Returns the USID as decoded bytes.
    default byte[] usidAsBytes() {
        return Base64.getDecoder().decode(this.usid());
    }

    /// Returns the USID as a long.
    default long usidAsLong() {
        return ByteBuffer.wrap(this.usidAsBytes()).getLong();
    }
}
