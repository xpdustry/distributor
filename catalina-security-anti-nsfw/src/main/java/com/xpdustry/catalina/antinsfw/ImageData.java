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
package com.xpdustry.catalina.antinsfw;

import com.xpdustry.distributor.api.geometry.ImmutablePoint2;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;
import org.jspecify.annotations.Nullable;

public sealed interface ImageData {

    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Canvas extends ImageData {

        static Canvas of(final ImmutablePixMap pixels, final Instant timestamp, final @Nullable MUUID author) {
            return CanvasImpl.of(pixels, timestamp, author);
        }

        ImmutablePixMap pixels();

        Instant timestamp();

        @Nullable MUUID author();
    }

    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Display extends ImageData {

        static Display of(final int resolution, final Map<ImmutablePoint2, Processor> processors) {
            return DisplayImpl.of(resolution, processors);
        }

        int resolution();

        Map<ImmutablePoint2, Processor> processors();

        @DistributorDataClass
        @Value.Immutable
        interface Processor {

            static Processor of(
                    final List<Instruction> instructions,
                    final Instant timestamp,
                    final List<ImmutablePoint2> links,
                    final @Nullable MUUID author) {
                return ProcessorImpl.of(instructions, timestamp, links, author);
            }

            List<Instruction> instructions();

            Instant timestamp();

            List<ImmutablePoint2> links();

            @Nullable MUUID author();
        }

        sealed interface Instruction {

            @DistributorDataClass
            @Value.Immutable
            non-sealed interface SetColor extends Instruction {

                int r();

                int g();

                int b();

                int a();
            }

            @DistributorDataClass
            @Value.Immutable
            non-sealed interface DrawPoly extends Instruction {

                List<ImmutablePoint2> points();
            }
        }
    }
}
