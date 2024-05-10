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
package com.xpdustry.distributor.api.window;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import mindustry.gen.Player;
import org.immutables.value.Value;

public sealed interface DisplayUnit {

    int asPixels(final Player player, final Axis axis);

    enum Axis {
        X,
        Y
    }

    @DistributorDataClass
    @Value.Immutable
    sealed interface Pixel extends DisplayUnit permits PixelImpl {

        Pixel ZERO = Pixel.of(0);

        static Pixel of(final int value) {
            return PixelImpl.of(value);
        }

        int getValue();

        @Override
        default int asPixels(final Player player, final Axis axis) {
            return getValue();
        }
    }
}
