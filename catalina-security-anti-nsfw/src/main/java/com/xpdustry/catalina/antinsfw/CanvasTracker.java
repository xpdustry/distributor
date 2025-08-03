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

import arc.graphics.Pixmap;
import arc.struct.IntMap;
import com.xpdustry.distributor.api.geometry.GroupingBuildingIndexer;
import com.xpdustry.distributor.api.geometry.GroupingFunction;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.PluginListener;
import java.time.Instant;
import java.util.Objects;
import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.world.blocks.logic.CanvasBlock;
import org.jspecify.annotations.Nullable;

final class CanvasTracker implements PluginListener {

    private final IntMap<Pixmap> previews = new IntMap<>();
    private final GroupingBuildingIndexer<ImageData.Canvas> indexer =
            GroupingBuildingIndexer.create(GroupingFunction.always());
    private final CatalinaSecurityAntiNSFWPlugin plugin;

    public CanvasTracker(final CatalinaSecurityAntiNSFWPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginLoad() {
        try {
            final var field = CanvasBlock.class.getDeclaredField("previewPixmap");
            field.setAccessible(true);
            for (final var block : Vars.content.blocks()) {
                if (block instanceof CanvasBlock) {
                    this.previews.put(block.id, (Pixmap) field.get(block));
                }
            }
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.plugin.addListener(new BuildingLifecycleListener<>(CanvasBlock.CanvasBuild.class) {
            @Override
            protected void onBuildingLifecycleEvent(
                    final CanvasBlock.CanvasBuild building, final @Nullable Player player, final Kind kind) {
                final var done =
                        switch (kind) {
                            case CREATE -> CanvasTracker.this.indexer.insert(
                                    NoHornyUtils.rx(building),
                                    NoHornyUtils.ry(building),
                                    building.block.size,
                                    CanvasTracker.this.data(building, player));
                            case UPDATE -> CanvasTracker.this.indexer.update(
                                    NoHornyUtils.rx(building),
                                    NoHornyUtils.ry(building),
                                    CanvasTracker.this.data(building, player));
                            case REMOVE -> CanvasTracker.this.indexer.remove(
                                            NoHornyUtils.rx(building), NoHornyUtils.ry(building))
                                    != null;
                        };
                assert done;
            }
        });
    }

    private ImageData.Canvas data(final CanvasBlock.CanvasBuild building, final @Nullable Player player) {
        final var block = ((CanvasBlock) building.block);
        final var pixmap = Objects.requireNonNull(this.previews.get(block.id));
        block.makePixmap(building.config(), pixmap);
        return ImageData.Canvas.of(
                ImmutablePixMap.from(pixmap), Instant.now(), player == null ? null : MUUID.from(player));
    }
}
