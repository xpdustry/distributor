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
import com.xpdustry.distributor.api.geometry.IndexedBuildingGroup;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

final class MindustryImageRendererImpl implements MindustryImageRenderer {

    private static final int PIXELS_PER_BLOCK = 32;

    @Override
    public BufferedImage render(final IndexedBuildingGroup<? extends MindustryImage> group) {
        final var image = new BufferedImage(
                group.w() * PIXELS_PER_BLOCK, group.h() * PIXELS_PER_BLOCK, BufferedImage.TYPE_INT_ARGB);
        try (final var scope = new GraphicsScope(image)) {
            scope.graphics().setColor(Color.BLACK);
            scope.graphics().fillRect(0, 0, image.getWidth(), image.getHeight());
            for (final var building : group.buildings()) {
                scope.graphics()
                        .drawImage(
                                this.render(building.data()),
                                (building.x() - group.x()) * PIXELS_PER_BLOCK,
                                (building.y() - group.y()) * PIXELS_PER_BLOCK,
                                building.s() * PIXELS_PER_BLOCK,
                                building.s() * PIXELS_PER_BLOCK,
                                null);
            }
        }
        // Invert y-axis, because Mindustry uses bottom-left as origin
        return this.invertYAxis(image);
    }

    @Override
    public BufferedImage render(final MindustryImage data) {
        return switch (data) {
            case MindustryImage.Canvas canvas -> canvas.pixels().toBufferedImage();
            case MindustryImage.Display display -> {
                final var image =
                        new BufferedImage(display.resolution(), display.resolution(), BufferedImage.TYPE_INT_ARGB);
                try (final var scope = new GraphicsScope(image)) {
                    scope.graphics().setColor(Color.BLACK);
                    for (final var processor : display.processors().values()) {
                        for (final var instruction : processor.instructions()) {
                            switch (instruction) {
                                case DrawInstruction.SetColor(int r, int g, int b, int a) ->
                                    scope.graphics().setColor(new Color(r, g, b, a));
                                case DrawInstruction.DrawRect(int x, int y, int w, int h) ->
                                    scope.graphics().fillRect(x, y, w, h);
                                case DrawInstruction.DrawPoly(var points) ->
                                    scope.graphics()
                                            .fillPolygon(
                                                    points.stream()
                                                            .mapToInt(ImmutablePoint2::x)
                                                            .toArray(),
                                                    points.stream()
                                                            .mapToInt(ImmutablePoint2::y)
                                                            .toArray(),
                                                    points.size());
                            }
                        }
                    }
                }
                yield image;
            }
        };
    }

    private BufferedImage invertYAxis(final BufferedImage image) {
        final var transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0.0, -image.getHeight());
        final var op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }
}
