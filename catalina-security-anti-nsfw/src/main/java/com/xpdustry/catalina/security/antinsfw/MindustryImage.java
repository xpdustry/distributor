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
import com.xpdustry.distributor.api.player.MUUID;
import java.time.Instant;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public sealed interface MindustryImage {

    int resolution();

    record Display(int resolution, Map<ImmutablePoint2, LogicProcessor> processors) implements MindustryImage {}

    record Canvas(ImmutablePixMap pixels, Instant timestamp, @Nullable MUUID author) implements MindustryImage {
        @Override
        public int resolution() {
            return Math.max(this.pixels.w(), this.pixels.h());
        }
    }
}
