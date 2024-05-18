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
package com.xpdustry.distributor.api.component.style;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import org.immutables.value.Value;

@DistributorDataClass
@Value.Immutable
public interface ComponentColor {

    ComponentColor RED = rgb(0xFF0000);
    ComponentColor GREEN = rgb(0x00FF00);
    ComponentColor BLUE = rgb(0x0000FF);
    ComponentColor YELLOW = rgb(0xFFFF00);
    ComponentColor CYAN = rgb(0x00FFFF);
    ComponentColor MAGENTA = rgb(0xFF00FF);
    ComponentColor WHITE = rgb(0xFFFFFF);
    ComponentColor BLACK = rgb(0x000000);

    static ComponentColor rgb(final int rgb) {
        return ComponentColorImpl.of(rgb & 0xFFFFFF);
    }

    static ComponentColor rgb(final int r, final int g, final int b) {
        return rgb(r << 16 | g << 8 | b);
    }

    static ComponentColor from(final java.awt.Color color) {
        return rgb(color.getRGB());
    }

    static ComponentColor from(final arc.graphics.Color color) {
        return rgb(color.rgb888());
    }

    int getRGB();

    default int getR() {
        return getRGB() >> 16 & 0xFF;
    }

    default int getG() {
        return getRGB() >> 8 & 0xFF;
    }

    default int getB() {
        return getRGB() & 0xFF;
    }
}
