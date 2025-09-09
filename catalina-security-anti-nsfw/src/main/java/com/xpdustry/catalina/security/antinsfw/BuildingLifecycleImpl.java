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

import arc.math.geom.Point2;
import arc.struct.IntMap;
import com.xpdustry.distributor.api.annotation.EventHandler;
import com.xpdustry.distributor.api.annotation.PluginAnnotationProcessor;
import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.util.ArrayList;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Player;
import mindustry.gen.Unitc;
import mindustry.world.blocks.ConstructBlock;
import org.jspecify.annotations.Nullable;

final class BuildingLifecycleImpl implements BuildingLifecycle {

    private final PluginAnnotationProcessor<EventSubscription> processor;

    public BuildingLifecycleImpl(final MindustryPlugin plugin) {
        this.processor = PluginAnnotationProcessor.events(plugin);
    }

    @Override
    public <B extends Building> EventSubscription subscribe(final Class<B> type, final Listener<B> listener) {
        return this.processor.process(new Inner<>(type, listener)).orElseThrow();
    }

    private static final class Inner<B extends Building> {

        private final IntMap<Boolean> tracking = new IntMap<>();
        private final Class<B> clazz;
        private final Listener<B> listener;

        public Inner(final Class<B> clazz, final Listener<B> listener) {
            this.clazz = clazz;
            this.listener = listener;
        }

        @EventHandler
        void onBuildingBuildEndEvent(final EventType.BlockBuildEndEvent event) {
            final var buildings = new ArrayList<Building>();
            buildings.add(event.tile.build);
            if (buildings.getFirst() instanceof ConstructBlock.ConstructBuild constructing
                    && constructing.prevBuild != null) {
                buildings.addAll(MindustryCollections.immutableList(constructing.prevBuild));
            }
            for (final var building : buildings) {
                if (!this.clazz.isInstance(building)) {
                    continue;
                }
                final var player = this.toPlayerOrNull(event.unit);
                if (event.breaking) {
                    this.listener.onBuildingLifecycleEvent(this.clazz.cast(building), Kind.REMOVE, player);
                } else {
                    this.listener.onBuildingLifecycleEvent(this.clazz.cast(building), Kind.INSERT, player);
                }
                this.tracking.remove(building.pos());
            }
        }

        @EventHandler
        void onBuildingConfigEvent(final EventType.ConfigEvent event) {
            if (this.clazz.isInstance(event.tile)) {
                this.listener.onBuildingLifecycleEvent(this.clazz.cast(event.tile), Kind.UPDATE, event.player);
            }
        }

        @EventHandler
        void onBuildingDestroyEvent(final EventType.BlockDestroyEvent event) {
            if (this.clazz.isInstance(event.tile.build)) {
                this.listener.onBuildingLifecycleEvent(this.clazz.cast(event.tile), Kind.REMOVE, null);
            }
        }

        @EventHandler
        void onBuildingBulletDestroyEvent(final EventType.BuildingBulletDestroyEvent event) {
            if (this.clazz.isInstance(event.build)) {
                this.listener.onBuildingLifecycleEvent(
                        this.clazz.cast(event.build), Kind.REMOVE, this.toPlayerOrNull(event.bullet.owner()));
            }
        }

        @EventHandler
        void onBuildingTeamChangeEvent(final EventType.BuildTeamChangeEvent event) {
            if (this.clazz.isInstance(event.build)) {
                this.listener.onBuildingLifecycleEvent(this.clazz.cast(event.build), Kind.UPDATE, null);
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
                    this.listener.onBuildingLifecycleEvent(this.clazz.cast(event.tile.build), Kind.UPDATE, null);
                } else {
                    this.listener.onBuildingLifecycleEvent(this.clazz.cast(event.tile.build), Kind.INSERT, null);
                }
            } else {
                this.listener.onBuildingLifecycleEvent(this.clazz.cast(event.tile.build), Kind.REMOVE, null);
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
