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

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import org.checkerframework.checker.nullness.qual.Nullable;

final class PlainComponentAppendable implements ComponentAppendable {

    private final StringBuilder builder = new StringBuilder();
    private final MetadataContainer context;
    private final ComponentRendererProvider provider;

    public PlainComponentAppendable(final MetadataContainer context, final ComponentRendererProvider provider) {
        this.context = context;
        this.provider = provider;
    }

    @Override
    public ComponentAppendable append(final Component component) {
        final var renderer = this.provider.getRenderer(component);
        if (renderer != null) renderer.render(component, this, this.context);
        return this;
    }

    @Override
    public ComponentAppendable append(final @Nullable CharSequence csq) {
        this.builder.append(csq);
        return this;
    }

    @Override
    public ComponentAppendable append(final @Nullable CharSequence csq, final int start, final int end) {
        this.builder.append(csq, start, end);
        return this;
    }

    @Override
    public ComponentAppendable append(final char c) {
        this.builder.append(c);
        return this;
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
