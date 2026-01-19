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
package com.xpdustry.distributor.api.event;

import arc.math.geom.Point2;
import arc.struct.IntMap;
import com.xpdustry.distributor.api.annotation.EventHandler;
import com.xpdustry.distributor.api.annotation.PluginAnnotationProcessor;
import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.util.ArrayList;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Player;
import mindustry.gen.Unitc;
import mindustry.world.blocks.ConstructBlock;
import org.jspecify.annotations.Nullable;

final class BuildingEventBusImpl implements BuildingEventBus {

    private final PluginAnnotationProcessor<EventSubscription> processor;

    BuildingEventBusImpl(final MindustryPlugin plugin) {
        this.processor = PluginAnnotationProcessor.events(plugin);
    }

    @Override
    public <B extends Building> EventSubscription subscribe(final Class<B> type, final Listener<B> listener) {
        return this.processor.process(new Inner<>(type, listener)).orElseThrow();
    }

    @SuppressWarnings("UnusedMethod")
    private static final class Inner<B extends Building> {

        private final IntMap<@Nullable Boolean> tracking = new IntMap<>();
        private final Class<B> clazz;
        private final Listener<B> listener;

        Inner(final Class<B> clazz, final Listener<B> listener) {
            this.clazz = clazz;
            this.listener = listener;
        }

        @EventHandler
        void onBuildingBuildEndEvent(final EventType.BlockBuildEndEvent event) {
            final var buildings = new ArrayList<Building>();
            buildings.add(event.tile.build);
            if (event.tile.build instanceof ConstructBlock.ConstructBuild constructing
                    && constructing.prevBuild != null) {
                buildings.addAll(MindustryCollections.immutableList(constructing.prevBuild));
            }
            for (final var building : buildings) {
                if (!this.clazz.isInstance(building)) {
                    continue;
                }
                final var player = this.toPlayerOrNull(event.unit);
                if (event.breaking) {
                    this.listener.onBuildingEvent(this.clazz.cast(building), Kind.DELETE, player);
                } else {
                    this.listener.onBuildingEvent(this.clazz.cast(building), Kind.CREATE, player);
                }
                this.tracking.remove(building.pos());
            }
        }

        @EventHandler
        void onBuildingConfigEvent(final EventType.ConfigEvent event) {
            if (this.clazz.isInstance(event.tile)) {
                this.listener.onBuildingEvent(this.clazz.cast(event.tile), Kind.UPDATE, event.player);
            }
        }

        @EventHandler
        void onBuildingDestroyEvent(final EventType.BlockDestroyEvent event) {
            if (this.clazz.isInstance(event.tile.build)) {
                this.listener.onBuildingEvent(this.clazz.cast(event.tile), Kind.DELETE, null);
            }
        }

        @EventHandler
        void onBuildingBulletDestroyEvent(final EventType.BuildingBulletDestroyEvent event) {
            if (this.clazz.isInstance(event.build)) {
                this.listener.onBuildingEvent(
                        this.clazz.cast(event.build), Kind.DELETE, this.toPlayerOrNull(event.bullet.owner()));
            }
        }

        @EventHandler
        void onBuildingTeamChangeEvent(final EventType.BuildTeamChangeEvent event) {
            if (this.clazz.isInstance(event.build)) {
                this.listener.onBuildingEvent(this.clazz.cast(event.build), Kind.UPDATE, null);
            }
        }

        @EventHandler
        void onBuildingChangingPostEvent(final EventType.TileChangeEvent event) {
            final var wasBuildingInstance = this.tracking.get(Point2.pack(event.tile.x, event.tile.y));
            if (wasBuildingInstance == null) {
                return;
            }
            if (this.clazz.isInstance(event.tile.build)) {
                if (wasBuildingInstance) {
                    this.listener.onBuildingEvent(this.clazz.cast(event.tile.build), Kind.UPDATE, null);
                } else {
                    this.listener.onBuildingEvent(this.clazz.cast(event.tile.build), Kind.CREATE, null);
                }
            } else {
                this.listener.onBuildingEvent(this.clazz.cast(event.tile.build), Kind.DELETE, null);
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
    }
}
