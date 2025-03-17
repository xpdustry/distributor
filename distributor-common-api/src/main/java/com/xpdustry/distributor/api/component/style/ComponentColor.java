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

import arc.graphics.Color;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import mindustry.graphics.Pal;
import org.immutables.value.Value;

/**
 * An immutable representation of a color for components.
 */
@DistributorDataClass
@Value.Immutable
public interface ComponentColor {

    ComponentColor WHITE = from(Color.white);
    ComponentColor LIGHT_GRAY = from(Color.lightGray);
    ComponentColor GRAY = from(Color.gray);
    ComponentColor DARK_GRAY = from(Color.darkGray);
    ComponentColor BLACK = from(Color.black);

    ComponentColor BLUE = from(Color.blue);
    ComponentColor NAVY = from(Color.navy);
    ComponentColor ROYAL = from(Color.royal);
    ComponentColor SLATE = from(Color.slate);
    ComponentColor SKY = from(Color.sky);
    ComponentColor CYAN = from(Color.cyan);
    ComponentColor TEAL = from(Color.teal);

    ComponentColor GREEN = from(Color.green);
    ComponentColor ACID = from(Color.acid);
    ComponentColor LIME = from(Color.lime);
    ComponentColor FOREST = from(Color.forest);
    ComponentColor OLIVE = from(Color.olive);

    ComponentColor YELLOW = from(Color.yellow);
    ComponentColor GOLD = from(Color.gold);
    ComponentColor GOLDENROD = from(Color.goldenrod);
    ComponentColor ORANGE = from(Color.orange);

    ComponentColor BROWN = from(Color.brown);
    ComponentColor TAN = from(Color.tan);
    ComponentColor BRICK = from(Color.brick);

    ComponentColor RED = from(Color.red);
    ComponentColor SCARLET = from(Color.scarlet);
    ComponentColor CRIMSON = from(Color.crimson);
    ComponentColor CORAL = from(Color.coral);
    ComponentColor SALMON = from(Color.salmon);
    ComponentColor PINK = from(Color.pink);
    ComponentColor MAGENTA = from(Color.magenta);

    ComponentColor PURPLE = from(Color.purple);
    ComponentColor VIOLET = from(Color.violet);
    ComponentColor MAROON = from(Color.maroon);

    ComponentColor ACCENT = from(Pal.accent);

    /**
     * Creates a new color from the given RGB value.
     *
     * @param rgb the RGB value
     * @return the component color
     */
    static ComponentColor rgb(final int rgb) {
        return ComponentColorImpl.of(rgb & 0xFFFFFF);
    }

    /**
     * Creates a new color from the given RGB values.
     *
     * @param r the red value
     * @param g the green value
     * @param b the blue value
     * @return the component color
     */
    static ComponentColor rgb(final int r, final int g, final int b) {
        return rgb(r << 16 | g << 8 | b);
    }

    /**
     * Creates a new color from the given AWT color.
     *
     * @param color the AWT color
     * @return the component color
     */
    static ComponentColor from(final java.awt.Color color) {
        return rgb(color.getRGB());
    }

    /**
     * Creates a new color from the given Arc color.
     *
     * @param color the Arc color
     * @return the component color
     */
    static ComponentColor from(final arc.graphics.Color color) {
        return rgb(color.rgb888());
    }

    /**
     * Returns the RGB value of this color.
     */
    int getRGB();

    /**
     * Returns the red value of this color.
     */
    default int getR() {
        return this.getRGB() >> 16 & 0xFF;
    }

    /**
     * Returns the green value of this color.
     */
    default int getG() {
        return this.getRGB() >> 8 & 0xFF;
    }

    /**
     * Returns the blue value of this color.
     */
    default int getB() {
        return this.getRGB() & 0xFF;
    }
}
