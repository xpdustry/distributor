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

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import com.xpdustry.distributor.internal.annotation.DistributorDataClassWithBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mindustry.world.Block;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@DistributorDataClassWithBuilder
@Value.Immutable
public sealed interface ImmutableSchematic permits ImmutableSchematicImpl {

    String NAME_TAG = "name";

    String DESCRIPTION_TAG = "description";

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

    @Value.Default
    default int getWidth() {
        return getTiles().stream()
                .mapToInt(tile -> tile.getX() + tile.getBlock().size)
                .max()
                .orElse(0);
    }

    @Value.Default
    default int getHeight() {
        return getTiles().stream()
                .mapToInt(tile -> tile.getY() + tile.getBlock().size)
                .max()
                .orElse(0);
    }

    Set<String> getLabels();

    Map<String, String> getTags();

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

        Builder setWidth(final int width);

        Builder setHeight(final int height);

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

        static Tile of(
                final int x,
                final int y,
                final Block block,
                final BlockRotation rotation,
                final @Nullable Object config) {
            return TileImpl.of(x, y, block, rotation, config);
        }

        int getX();

        int getY();

        Block getBlock();

        BlockRotation getRotation();

        @Nullable Object getConfig();
    }
}
