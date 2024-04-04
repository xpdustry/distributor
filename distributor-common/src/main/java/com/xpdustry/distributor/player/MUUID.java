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

import java.util.Base64;
import java.util.Objects;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.jspecify.annotations.Nullable;

/**
 * The mindustry identity format. A combination of a UUID and a USID.
 */
public final class MUUID {

    private final String uuid;
    private final String usid;

    private MUUID(final String uuid, final String usid) {
        checkUuid(uuid);
        checkUsid(usid);
        this.uuid = uuid;
        this.usid = usid;
    }

    /**
     * Creates a new MUUID from a UUID and USID.
     *
     * @param uuid the UUID
     * @param usid the USID
     * @return the MUUID
     */
    public static MUUID of(final String uuid, final String usid) {
        return new MUUID(uuid, usid);
    }

    /**
     * Creates a new MUUID from a {@link Player}.
     *
     * @param player the player
     * @return the MUUID
     */
    public static MUUID of(final Player player) {
        return new MUUID(player.uuid(), player.usid());
    }

    /**
     * Creates a new MUUID from a {@link Administration.PlayerInfo}.
     *
     * @param info the player info
     * @return the MUUID
     */
    public static MUUID of(final Administration.PlayerInfo info) {
        return new MUUID(info.id, info.adminUsid);
    }

    /**
     * Returns whether the given string is a valid UUID.
     *
     * @param uuid the UUID to check
     * @return true if the given string is a valid UUID, false otherwise
     */
    public static boolean isUuid(final String uuid) {
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
    public static void checkUuid(final String uuid) {
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
    public static boolean isUsid(final String usid) {
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
    public static void checkUsid(final String usid) {
        if (!isUsid(usid)) {
            throw new IllegalArgumentException(String.format("Invalid USID: %s", usid));
        }
    }

    /**
     * Returns the UUID.
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Returns the UUID as decoded bytes.
     */
    public byte[] getDecodedUuid() {
        return Base64.getDecoder().decode(this.uuid);
    }

    /**
     * Returns the USID.
     */
    public String getUsid() {
        return this.usid;
    }

    /**
     * Returns the USID as decoded bytes.
     */
    public byte[] getDecodedUsid() {
        return Base64.getDecoder().decode(this.usid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.usid);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (obj instanceof final MUUID muuid && this.uuid.equals(muuid.uuid) && this.usid.equals(muuid.usid));
    }

    @Override
    public String toString() {
        return "MUUID{" + "uuid='" + this.uuid + '\'' + ", usid='" + this.usid + '\'' + '}';
    }
}
