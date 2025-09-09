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

import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.api.geometry.GroupingBuildingIndexer;
import com.xpdustry.distributor.api.geometry.GroupingFunction;
import com.xpdustry.distributor.api.geometry.ImmutablePoint2;
import com.xpdustry.distributor.api.geometry.IndexedBuilding;
import com.xpdustry.distributor.api.key.Key;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.PluginListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import mindustry.gen.Player;
import mindustry.logic.LExecutor;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.logic.LogicDisplay;
import org.jspecify.annotations.Nullable;

final class DisplayTracker implements PluginListener {

    private static final Key<Integer> PROCESSOR_SEARCH_RADIUS =
            Key.of("nohorny", "display.processor-search-radius", Integer.class);

    private final GroupingBuildingIndexer<MindustryImage.Display> displays =
            GroupingBuildingIndexer.create(GroupingFunction.always());

    private final GroupingBuildingIndexer<LogicProcessor> processors =
            GroupingBuildingIndexer.create(GroupingFunction.single());

    private final BuildingLifecycle lifecycle;
    private final Executor executor;
    private final KeyContainer configuration;

    DisplayTracker(final BuildingLifecycle lifecycle, final Executor executor, final KeyContainer configuration) {
        this.lifecycle = lifecycle;
        this.executor = executor;
        this.configuration = configuration;
    }

    @Override
    public void onPluginInit() {
        this.lifecycle.subscribe(LogicDisplay.LogicDisplayBuild.class, this::onScreenLifecycleEvent);
        this.lifecycle.subscribe(LogicBlock.LogicBuild.class, this::onLogicDisplayLifecycleEvent);
    }

    private void onScreenLifecycleEvent(
            final LogicDisplay.LogicDisplayBuild building,
            final BuildingLifecycle.Kind kind,
            final @Nullable Player player) {
        final int tx = building.tileX();
        final int ty = building.tileY();
        switch (kind) {
            case INSERT, UPDATE -> {
                final int rx = NoHornyUtils.rx(building);
                final int ry = NoHornyUtils.ry(building);
                final int size = building.block.size;
                final int radius =
                        this.configuration.getOptional(PROCESSOR_SEARCH_RADIUS).orElse(10);
                final int resolution = ((LogicDisplay) building.block).displaySize;

                this.executor.execute(() -> {
                    final var processors = this.processors
                            .selectAll(tx - Math.ceilDiv(radius, 2), ty - Math.ceilDiv(radius, 2), radius, radius)
                            .stream()
                            .filter(entry -> entry.data().links().stream().anyMatch(link -> {
                                final int xd = link.x() - rx;
                                final int yd = link.y() - ry;
                                return (xd * xd) + (yd * yd) < radius * radius;
                            }))
                            .collect(Collectors.toUnmodifiableMap(
                                    entry -> new ImmutablePoint2(entry.x(), entry.y()), IndexedBuilding::data));
                    this.displays.upsert(rx, ry, size, new MindustryImage.Display(resolution, processors));
                });
            }
            case REMOVE -> this.executor.execute(() -> this.displays.remove(tx, ty));
        }
    }

    private void onLogicDisplayLifecycleEvent(
            final LogicBlock.LogicBuild building, final BuildingLifecycle.Kind kind, final @Nullable Player player) {
        final var rx = NoHornyUtils.rx(building);
        final var ry = NoHornyUtils.ry(building);
        switch (kind) {
            case INSERT, UPDATE -> {
                final var muuid = player == null ? null : MUUID.from(player);
                final var size = building.block.size;
                final var links = MindustryCollections.immutableList(building.links).stream()
                        .filter(Objects::nonNull)
                        .map(link -> new ImmutablePoint2(link.x, link.y))
                        .toList();
                final var instructions = this.instructions(building.executor);
                final var data = new LogicProcessor(instructions, Instant.now(), links, muuid);

                this.executor.execute(() -> {
                    final var processor = this.processors.upsert(rx, ry, size, data);
                    this.forEachDisplayUpdateProcessorLink(links, processor, kind);
                });
            }
            case REMOVE ->
                this.executor.execute(() -> {
                    final var processor = this.processors.remove(rx, ry);
                    if (processor != null) {
                        this.forEachDisplayUpdateProcessorLink(processor.data().links(), processor, kind);
                    }
                });
        }
    }

    private List<DrawInstruction> instructions(final LExecutor executor) {
        final var result = new ArrayList<DrawInstruction>();
        for (final var i : executor.instructions) {
            if (!(i instanceof LExecutor.DrawI draw)) {
                continue;
            }
            final DrawInstruction instruction;
            switch (draw.type) {
                case LogicDisplay.commandColor -> {
                    final int r = this.normalize(draw.x.numi());
                    final int g = this.normalize(draw.y.numi());
                    final int b = this.normalize(draw.p1.numi());
                    final int a = this.normalize(draw.p2.numi());
                    instruction = new DrawInstruction.SetColor(r, g, b, a);
                }
                case LogicDisplay.commandRect -> {
                    final int x = draw.x.numi();
                    final int y = draw.y.numi();
                    final int w = draw.p1.numi();
                    final int h = draw.p2.numi();
                    instruction = new DrawInstruction.DrawRect(x, y, w, h);
                }
                case LogicDisplay.commandTriangle -> {
                    final int x1 = draw.x.numi();
                    final int y1 = draw.y.numi();
                    final int x2 = draw.p1.numi();
                    final int y2 = draw.p2.numi();
                    final int x3 = draw.p3.numi();
                    final int y3 = draw.p4.numi();
                    instruction = new DrawInstruction.DrawPoly(
                            new ImmutablePoint2(x1, y1), new ImmutablePoint2(x2, y2), new ImmutablePoint2(x3, y3));
                }
                default -> {
                    continue;
                }
            }
            result.add(instruction);
        }
        return result;
    }

    private int normalize(final int value) {
        final var result = value % 256;
        return result < 0 ? result + 256 : result;
    }

    private void forEachDisplayUpdateProcessorLink(
            final Collection<ImmutablePoint2> displays,
            final IndexedBuilding<LogicProcessor> processor,
            final BuildingLifecycle.Kind kind) {
        for (final var location : displays) {
            final var display = this.displays.select(location.x(), location.y());
            if (display == null) {
                continue;
            }
            final var processors = new HashMap<>(display.data().processors());
            final var point = new ImmutablePoint2(processor.x(), processor.y());
            switch (kind) {
                case INSERT, UPDATE -> processors.put(point, processor.data());
                case REMOVE -> processors.remove(point);
            }
            this.displays.update(
                    display.x(),
                    display.y(),
                    new MindustryImage.Display(display.data().resolution(), Collections.unmodifiableMap(processors)));
        }
    }
}
