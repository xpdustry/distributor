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

import arc.graphics.Pixmap;
import arc.struct.IntMap;
import com.xpdustry.distributor.api.geometry.GroupingBuildingIndexer;
import com.xpdustry.distributor.api.geometry.GroupingFunction;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.PluginListener;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executor;
import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.world.blocks.logic.CanvasBlock;
import org.jspecify.annotations.Nullable;

final class CanvasTracker implements PluginListener {

    private final IntMap<Pixmap> previews = new IntMap<>();
    private final GroupingBuildingIndexer<MindustryImage.Canvas> indexer =
            GroupingBuildingIndexer.create(GroupingFunction.always());

    private final BuildingLifecycle lifecycle;
    private final Executor executor;

    public CanvasTracker(final BuildingLifecycle lifecycle, final Executor executor) {
        this.lifecycle = lifecycle;
        this.executor = executor;
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

        this.lifecycle.subscribe(CanvasBlock.CanvasBuild.class, this::onCanvasLifecycleEvent);
    }

    private void onCanvasLifecycleEvent(
            final CanvasBlock.CanvasBuild building, final BuildingLifecycle.Kind kind, final @Nullable Player player) {
        final var x = NoHornyUtils.rx(building);
        final var y = NoHornyUtils.ry(building);
        switch (kind) {
            case INSERT, UPDATE -> {
                final var data = this.data(building, player);
                final var size = building.block.size;
                this.executor.execute(() -> this.indexer.upsert(x, y, size, data));
            }
            case REMOVE -> this.executor.execute(() -> this.indexer.remove(x, y));
        }
    }

    private MindustryImage.Canvas data(final CanvasBlock.CanvasBuild building, final @Nullable Player player) {
        final var block = ((CanvasBlock) building.block);
        final var pixmap = Objects.requireNonNull(this.previews.get(block.id));
        block.makePixmap(building.config(), pixmap);
        final var muuid = player == null ? null : MUUID.from(player);
        return new MindustryImage.Canvas(ImmutablePixMap.from(pixmap), Instant.now(), muuid);
    }
}
