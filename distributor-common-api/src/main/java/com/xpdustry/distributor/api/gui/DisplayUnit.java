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
package com.xpdustry.distributor.api.gui;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import mindustry.gen.Player;
import org.immutables.value.Value;

/**
 * A generic unit for window managers relying on the player's screen size.
 */
public interface DisplayUnit {

    /**
     * Converts this unit to pixels based on the player's screen size.
     *
     * @param player the player
     * @param axis   the axis to convert
     * @return the converted value
     */
    int asPixels(final Player player, final Axis axis);

    /**
     * Represents an axis.
     */
    enum Axis {
        X,
        Y
    }

    /**
     * Represents a unit in raw pixels.
     */
    @DistributorDataClass
    @Value.Immutable
    interface Pixel extends DisplayUnit {

        Pixel ZERO = Pixel.of(0);

        /**
         * Creates a new {@link Pixel} instance with the given value.
         *
         * @param value the value
         * @return the new instance
         */
        static Pixel of(final int value) {
            return PixelImpl.of(value);
        }

        /**
         * Returns the pixels.
         */
        int getPixels();

        @Override
        default int asPixels(final Player player, final Axis axis) {
            return this.getPixels();
        }
    }
}
