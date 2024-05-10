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
package com.xpdustry.distributor.api.content;

import arc.struct.StringMap;
import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import com.xpdustry.distributor.internal.annotation.DistributorDataClassWithBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mindustry.game.Schematic;
import mindustry.type.Item;
import mindustry.world.Block;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@DistributorDataClassWithBuilder
@Value.Immutable
public sealed interface ImmutableSchematic permits ImmutableSchematicImpl {

    String NAME_TAG = "name";

    String DESCRIPTION_TAG = "description";

    static Schematic toSchematic(final ImmutableSchematic schematic) {
        final var copy = new Schematic(
                schematic.getTiles().stream().map(Tile::toStile).collect(MindustryCollections.collectToSeq()),
                schematic.getTags().entrySet().stream()
                        .collect(MindustryCollections.collectToObjectMap(
                                Map.Entry::getKey, Map.Entry::getValue, StringMap::new)),
                schematic.getWidth(),
                schematic.getHeight());
        copy.labels.addAll(schematic.getLabels());
        return copy;
    }

    static ImmutableSchematic from(final Schematic schematic) {
        return ImmutableSchematic.builder()
                .setTiles(MindustryCollections.immutableList(schematic.tiles).stream()
                        .map(Tile::from)
                        .toList())
                .setTags(MindustryCollections.immutableMap(schematic.tags))
                .setLabels(MindustryCollections.immutableList(schematic.labels))
                .build();
    }

    static Builder builder(final ImmutableSchematic schematic) {
        return ImmutableSchematicImpl.builder().from(schematic);
    }

    static Builder builder() {
        return ImmutableSchematicImpl.builder();
    }

    @Value.Derived
    default String getName() {
        return this.getTags().getOrDefault(NAME_TAG, "unknown");
    }

    @Value.Derived
    default String getDescription() {
        return this.getTags().getOrDefault(DESCRIPTION_TAG, "");
    }

    List<Tile> getTiles();

    @Value.Lazy
    default int getWidth() {
        return getTiles().stream()
                .mapToInt(tile -> tile.getX() + tile.getBlock().size)
                .max()
                .orElse(0);
    }

    @Value.Lazy
    default int getHeight() {
        return getTiles().stream()
                .mapToInt(tile -> tile.getY() + tile.getBlock().size)
                .max()
                .orElse(0);
    }

    Set<String> getLabels();

    Map<String, String> getTags();

    @Value.Lazy
    default Map<Item, Integer> getRequirements() {
        final Map<Item, Integer> requirements = new HashMap<>();
        for (final var tile : getTiles()) {
            for (final var stack : tile.getBlock().requirements) {
                requirements.compute(
                        stack.item, (item, amount) -> amount == null ? stack.amount : amount + stack.amount);
            }
        }
        return Collections.unmodifiableMap(requirements);
    }

    interface Builder {

        default Builder setName(final String name) {
            return putTag(NAME_TAG, name);
        }

        default Builder setDescription(final String description) {
            return putTag(DESCRIPTION_TAG, description);
        }

        Builder setTiles(final Iterable<? extends Tile> tiles);

        Builder addTile(final Tile tile);

        Builder addAllTiles(final Iterable<? extends Tile> tiles);

        Builder setLabels(final Iterable<String> labels);

        Builder addLabel(final String label);

        Builder addAllLabels(final Iterable<String> labels);

        Builder setTags(final Map<String, ? extends String> tags);

        Builder putTag(final String key, final String value);

        Builder putAllTags(final Map<String, ? extends String> tags);

        ImmutableSchematic build();
    }

    @DistributorDataClass
    @Value.Immutable
    sealed interface Tile permits TileImpl {

        static Schematic.Stile toStile(final Tile tile) {
            return new Schematic.Stile(tile.getBlock(), tile.getX(), tile.getY(), tile.getConfig(), (byte)
                    tile.getRotation().ordinal());
        }

        static Tile from(final Schematic.Stile stile) {
            return Tile.of(stile.x, stile.y, stile.block, BlockRotation.from(stile.rotation), stile.config);
        }

        static Tile of(
                final int x,
                final int y,
                final Block block,
                final BlockRotation rotation,
                final @Nullable Object config) {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Tile coordinates must be non-negative.");
            }
            return TileImpl.of(x, y, block, rotation, config);
        }

        int getX();

        int getY();

        Block getBlock();

        BlockRotation getRotation();

        @Nullable Object getConfig();
    }
}
