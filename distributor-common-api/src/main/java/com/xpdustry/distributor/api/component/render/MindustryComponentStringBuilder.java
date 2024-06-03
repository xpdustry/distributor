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

import arc.struct.IntSeq;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import org.checkerframework.checker.nullness.qual.Nullable;

final class MindustryComponentStringBuilder implements ComponentStringBuilder {

    private final IntSeq colors = new IntSeq();
    private final StringBuilder builder = new StringBuilder();
    private final MetadataContainer context;
    private final ComponentRendererProvider provider;

    MindustryComponentStringBuilder(final MetadataContainer context, final ComponentRendererProvider provider) {
        this.context = context;
        this.provider = provider;
    }

    @Override
    public MetadataContainer getContext() {
        return context;
    }

    @Override
    public ComponentStringBuilder append(final Component component) {
        {
            var color = component.getTextStyle().getTextColor();
            final var rgb = color == null ? -1 : color.getRGB();
            final var previous = colors.isEmpty() ? -1 : colors.peek();
            colors.add(rgb);
            if (rgb != -1 && rgb != previous) {
                builder.append("[#").append(String.format("%06X", rgb)).append(']');
            }
        }

        final var renderer = provider.getRenderer(component);
        if (renderer != null) renderer.render(component, this);

        {
            final var popped = colors.pop();
            final var previous = colors.isEmpty() ? -1 : colors.peek();
            if (popped != -1 && popped != previous) {
                builder.append("[]");
            }
        }

        return this;
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq) {
        return append(csq, 0, csq == null ? 0 : csq.length());
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq, final int start, final int end) {
        if (csq == null) {
            builder.append("null");
        } else {
            for (int i = start; i < end; i++) {
                append(csq.charAt(i));
            }
        }
        return this;
    }

    @Override
    public ComponentStringBuilder append(final char c) {
        if (c == '[') {
            builder.append("[[");
        } else {
            builder.append(c);
        }
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
