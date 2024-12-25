/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.api.player;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import java.util.zip.CRC32;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.immutables.value.Value;

/**
 * The mindustry identity format. A combination of a UUID and a USID.
 */
@DistributorDataClass
@Value.Immutable
public interface MUUID {

    /**
     * Creates a new MUUID from a UUID and USID.
     *
     * @param uuid the UUID
     * @param usid the USID
     * @return the MUUID
     */
    static MUUID of(final String uuid, final String usid) {
        checkUuid(uuid);
        checkUsid(usid);
        return MUUIDImpl.of(uuid, usid);
    }

    /**
     * Creates a new MUUID from a long UUID and long USID.
     *
     * @param uuid the UUID
     * @param usid the USID
     * @return the MUUID
     */
    static MUUID of(final long uuid, final long usid) {
        final var uuidBuffer = ByteBuffer.allocate(16);
        uuidBuffer.putLong(uuid);

        final var crc32 = new CRC32();
        crc32.update(uuidBuffer.array(), 0, 8);
        uuidBuffer.putLong(crc32.getValue());

        final var usidBuffer = ByteBuffer.allocate(8);
        usidBuffer.putLong(usid);

        return MUUIDImpl.of(
                Base64.getEncoder().encodeToString(uuidBuffer.array()),
                Base64.getEncoder().encodeToString(usidBuffer.array()));
    }

    /**
     * Creates a new MUUID from a {@link Player}.
     *
     * @param player the player
     * @return the MUUID
     */
    static MUUID from(final Player player) {
        return of(player.uuid(), player.usid());
    }

    /**
     * Creates a new MUUID from a {@link Administration.PlayerInfo}.
     *
     * @param info the player info
     * @return the MUUID
     */
    static MUUID from(final Administration.PlayerInfo info) {
        return of(info.id, info.adminUsid);
    }

    /**
     * Returns whether the given string is a valid UUID.
     *
     * @param uuid the UUID to check
     * @return true if the given string is a valid UUID, false otherwise
     */
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

    /**
     * Throws an {@link IllegalArgumentException} if the given string is not a valid UUID.
     */
    static void checkUuid(final String uuid) {
        if (!isUuid(uuid)) {
            throw new IllegalArgumentException(String.format("Invalid UUID: %s", uuid));
        }
    }

    /**
     * Returns whether the given string is a valid USID.
     *
     * @param usid the USID to check
     * @return true if the given string is a valid USID, false otherwise
     */
    static boolean isUsid(final String usid) {
        if (usid.length() % 4 != 0) return false;
        try {
            final var bytes = Base64.getDecoder().decode(usid);
            return bytes.length == 8;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the given string is not a valid USID.
     */
    static void checkUsid(final String usid) {
        if (!isUsid(usid)) {
            throw new IllegalArgumentException(String.format("Invalid USID: %s", usid));
        }
    }

    /**
     * Returns the UUID.
     */
    String getUuid();

    /**
     * Returns the UUID as decoded bytes.
     */
    default byte[] getUuidAsBytes() {
        return Base64.getDecoder().decode(this.getUuid());
    }

    /**
     * Returns the UUID as a long. Beware the long does not include the crc32 checksum.
     */
    default long getUuidAsLong() {
        return ByteBuffer.wrap(this.getUuidAsBytes()).getLong();
    }

    /**
     * Returns the USID.
     */
    String getUsid();

    /**
     * Returns the USID as decoded bytes.
     */
    default byte[] getUsidAsBytes() {
        return Base64.getDecoder().decode(this.getUsid());
    }

    /**
     * Returns the USID as a long.
     */
    default long getUsidAsLong() {
        return ByteBuffer.wrap(this.getUsidAsBytes()).getLong();
    }

    /**
     * Converts the uuid of this muuid into a real {@link UUID}, version 8.
     *
     * @see <a href="https://www.ietf.org/archive/id/draft-ietf-uuidrev-rfc4122bis-11.html#name-uuid-version-8">UUID v8 spec</a>
     */
    default UUID toRealUUID() {
        final var buffer = ByteBuffer.allocate(16);
        final var uuid = this.getUuidAsBytes();
        buffer.put(uuid, 0, 4); // First 4 bytes
        buffer.putShort((short) 0); // Next 2 bytes
        buffer.putShort((short) 0x8000); // Version is 4 bits, put set to v8
        buffer.putShort((short) 0x8000); // Variant is 2 bits, put set to IETF
        buffer.putShort((short) 0); // Next 2 bytes
        buffer.put(uuid, 4, 4); // Last 4 bytes
        buffer.flip();
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
