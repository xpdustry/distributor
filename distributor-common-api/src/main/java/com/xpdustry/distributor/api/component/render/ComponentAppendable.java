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
package com.xpdustry.distributor.api.component.render;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ComponentAppendable extends Appendable {

    static ComponentAppendable plain(final MetadataContainer metadata, final ComponentRendererProvider provider) {
        return new PlainComponentAppendable(metadata, provider);
    }

    static ComponentAppendable plain(final MetadataContainer metadata) {
        return new PlainComponentAppendable(metadata, DistributorProvider.get().getComponentRendererProvider());
    }

    static ComponentAppendable ansi(final MetadataContainer metadata, final ComponentRendererProvider provider) {
        return new AnsiComponentAppendable(metadata, provider);
    }

    static ComponentAppendable ansi(final MetadataContainer metadata) {
        return new AnsiComponentAppendable(metadata, DistributorProvider.get().getComponentRendererProvider());
    }

    static ComponentAppendable mindustry(final MetadataContainer metadata, final ComponentRendererProvider provider) {
        return new MindustryComponentAppendable(metadata, provider);
    }

    static ComponentAppendable mindustry(final MetadataContainer metadata) {
        return new MindustryComponentAppendable(
                metadata, DistributorProvider.get().getComponentRendererProvider());
    }

    MetadataContainer getContext();

    ComponentAppendable append(final Component component);

    @Override
    ComponentAppendable append(final @Nullable CharSequence csq);

    @Override
    ComponentAppendable append(final @Nullable CharSequence csq, final int start, final int end);

    @Override
    ComponentAppendable append(final char c);

    @Override
    String toString();
}
