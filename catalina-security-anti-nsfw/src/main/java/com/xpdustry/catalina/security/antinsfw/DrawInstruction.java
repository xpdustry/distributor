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
package com.xpdustry.catalina.security.antinsfw;

import com.xpdustry.distributor.api.geometry.ImmutablePoint2;
import java.util.Arrays;
import java.util.List;

public sealed interface DrawInstruction {

    record SetColor(int r, int g, int b, int a) implements DrawInstruction {

        public SetColor(int r, int g, int b, int a) {
            this.r = normalize(r);
            this.g = normalize(g);
            this.b = normalize(b);
            this.a = normalize(a);
        }

        private static int normalize(final int value) {
            final var result = value % 256;
            return result < 0 ? result + 256 : result;
        }
    }

    record DrawRect(int x, int y, int w, int h) implements DrawInstruction {

        public DrawRect {
            if (w <= 0) {
                throw new IllegalArgumentException("Expected 'w' to be greater than 0, got " + w + " instead");
            }
            if (h <= 0) {
                throw new IllegalArgumentException("Expected 'h' to be greater than 0, got " + w + " instead");
            }
        }
    }

    record DrawPoly(List<ImmutablePoint2> points) implements DrawInstruction {

        public DrawPoly(final List<ImmutablePoint2> points) {
            if (points == null) {
                throw new NullPointerException("points must not be null");
            }
            if (points.size() < 3) {
                throw new IllegalArgumentException("Expected at least 3 points, got " + points.size());
            }
            this.points = List.copyOf(points);
        }

        public DrawPoly(final ImmutablePoint2... point2) {
            this(Arrays.asList(point2));
        }
    }
}
