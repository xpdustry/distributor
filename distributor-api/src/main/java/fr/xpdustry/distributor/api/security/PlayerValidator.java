/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.security;

import fr.xpdustry.distributor.api.util.MUUID;

/**
 * A service to check player identities based on usids since uuids are very easy to steal.
 */
public interface PlayerValidator {

    /**
     * Checks whether a player muuid is valid.
     *
     * @param muuid the player's muuid.
     * @return {@code true} if the muuid is valid, {@code false} if invalid or unknown.
     */
    boolean isValid(final MUUID muuid);

    /**
     * Checks whether a player uuid is bound to any muuid inside the validator.
     *
     * @param uuid the player's uuid.
     * @return {@code true} if the uuid is bound to a muuid, {@code false} otherwise.
     */
    boolean contains(final String uuid);

    /**
     * Checks whether a player muuid is present inside this validator.
     *
     * @param muuid the player's muuid.
     * @return {@code true} if the muuid is present, {@code false} otherwise.
     */
    boolean contains(final MUUID muuid);

    /**
     * Marks the given muuid as valid.
     *
     * @param muuid the player's muuid.
     */
    void validate(final MUUID muuid);

    /**
     * Marks the given muuid as invalid.
     *
     * @param muuid the player's muuid.
     */
    void invalidate(final MUUID muuid);

    /**
     * Marks all muuid with the given uuid as invalid.
     *
     * @param uuid the player's uuid.
     */
    void invalidate(final String uuid);

    /**
     * Invalidates all muuids.
     */
    void invalidateAll();

    /**
     * Removes all muuid validation statuses with the given uuid from the validator.
     *
     * @param uuid the player's uuid.
     */
    void remove(final String uuid);

    /**
     * Removes muuid validation statuses from the validator.
     *
     * @param muuid the player's muuid.
     */
    void remove(final MUUID muuid);

    /**
     * Removes all muuid validation statuses from the validator.
     */
    void removeAll();
}
