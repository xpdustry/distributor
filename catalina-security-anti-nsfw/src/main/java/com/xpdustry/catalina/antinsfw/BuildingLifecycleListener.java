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

import arc.math.geom.Point2;
import arc.struct.IntMap;
import com.xpdustry.distributor.api.annotation.EventHandler;
import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.api.plugin.PluginListener;
import java.util.ArrayList;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Player;
import mindustry.gen.Unitc;
import mindustry.world.blocks.ConstructBlock;
import org.jspecify.annotations.Nullable;

public class BuildingLifecycleListener<B extends Building> implements PluginListener {

    private final IntMap<Boolean> tracking = new IntMap<>();
    private final Class<B> clazz;

    public BuildingLifecycleListener(final Class<B> clazz) {
        this.clazz = clazz;
    }

    protected void onBuildingLifecycleEvent(final B building, final @Nullable Player player, final Kind kind) {}

    @EventHandler
    void onBuildingBuildEndEvent(final EventType.BlockBuildEndEvent event) {
        final var buildings = new ArrayList<Building>();
        buildings.add(event.tile.build);
        if (buildings.get(0) instanceof ConstructBlock.ConstructBuild constructing && constructing.prevBuild != null) {
            buildings.addAll(MindustryCollections.immutableList(constructing.prevBuild));
        }
        for (final var building : buildings) {
            if (!this.clazz.isInstance(building)) {
                continue;
            }
            final var player = this.toPlayerOrNull(event.unit);
            if (event.breaking) {
                this.onBuildingLifecycleEvent(this.clazz.cast(building), player, Kind.REMOVE);
            } else {
                this.onBuildingLifecycleEvent(this.clazz.cast(building), player, Kind.CREATE);
            }
            this.tracking.remove(building.pos());
        }
    }

    @EventHandler
    void onBuildingConfigEvent(final EventType.ConfigEvent event) {
        if (this.clazz.isInstance(event.tile)) {
            this.onBuildingLifecycleEvent(this.clazz.cast(event.tile), event.player, Kind.UPDATE);
            this.tracking.remove(event.tile.pos());
        }
    }

    @EventHandler
    void onBuildingDestroyEvent(final EventType.BlockDestroyEvent event) {
        if (this.clazz.isInstance(event.tile.build)) {
            this.onBuildingLifecycleEvent(this.clazz.cast(event.tile), null, Kind.REMOVE);
            this.tracking.remove(event.tile.pos());
        }
    }

    @EventHandler
    void onBuildingBulletDestroyEvent(final EventType.BuildingBulletDestroyEvent event) {
        if (this.clazz.isInstance(event.build)) {
            this.onBuildingLifecycleEvent(
                    this.clazz.cast(event.build), this.toPlayerOrNull(event.bullet.owner()), Kind.REMOVE);
            this.tracking.remove(event.build.pos());
        }
    }

    @EventHandler
    void onBuildingTeamChangeEvent(final EventType.BuildTeamChangeEvent event) {
        if (this.clazz.isInstance(event.build)) {
            this.onBuildingLifecycleEvent(this.clazz.cast(event.build), null, Kind.UPDATE);
            this.tracking.remove(event.build.pos());
        }
    }

    @EventHandler
    void onBuildingChangingPreEvent(final EventType.TilePreChangeEvent event) {
        this.tracking.put(Point2.pack(event.tile.x, event.tile.y), this.clazz.isInstance(event.tile.build));
    }

    @EventHandler
    void onBuildingChangingPostEvent(final EventType.TileChangeEvent event) {
        final var wasBuildingInstance = this.tracking.get(Point2.pack(event.tile.x, event.tile.y));
        if (wasBuildingInstance == null) {
            return;
        }
        if (this.clazz.isInstance(event.tile.build)) {
            if (wasBuildingInstance) {
                this.onBuildingLifecycleEvent(this.clazz.cast(event.tile.build), null, Kind.UPDATE);
            } else {
                this.onBuildingLifecycleEvent(this.clazz.cast(event.tile.build), null, Kind.CREATE);
            }
        } else {
            this.onBuildingLifecycleEvent(this.clazz.cast(event.tile.build), null, Kind.REMOVE);
        }
        this.tracking.remove(event.tile.build.pos());
    }

    @EventHandler
    void onNewGameEvent(final EventType.StateChangeEvent event) {
        if (event.from == GameState.State.menu
                && (event.to == GameState.State.playing || event.to == GameState.State.paused)) {
            this.tracking.clear();
        }
    }

    private @Nullable Player toPlayerOrNull(final Entityc entity) {
        if (entity instanceof Player player) {
            return player;
        } else if (entity instanceof Unitc unit) {
            return unit.getPlayer();
        } else {
            return null;
        }
    }

    protected enum Kind {
        CREATE,
        UPDATE,
        REMOVE,
    }
}
