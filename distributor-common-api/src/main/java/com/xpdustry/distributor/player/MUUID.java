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
package com.xpdustry.distributor.player;

import com.xpdustry.distributor.internal.DistributorDataClass;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.immutables.value.Value;

/**
 * The mindustry identity format. A combination of a UUID and a USID.
 */
@DistributorDataClass
@Value.Immutable
public sealed interface MUUID permits MUUIDImpl {

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
        try {
            final var bytes = Base64.getDecoder().decode(uuid);
            return bytes.length == 16;
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
    default byte[] getDecodedUuid() {
        return Base64.getDecoder().decode(this.getUuid());
    }

    /**
     * Returns the USID.
     */
    String getUsid();

    /**
     * Returns the USID as decoded bytes.
     */
    default byte[] getDecodedUsid() {
        return Base64.getDecoder().decode(this.getUsid());
    }

    /**
     * TODO important doc
     * @see <a href="https://www.ietf.org/archive/id/draft-ietf-uuidrev-rfc4122bis-11.html#name-uuid-version-8">UUID v8 spec</a>
     */
    default UUID toRealUUID() {
        final var buffer = ByteBuffer.allocate(16);
        final var uuid = getDecodedUuid();
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
