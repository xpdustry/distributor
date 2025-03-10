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
import com.xpdustry.distributor.api.key.KeyContainer;
import org.jspecify.annotations.Nullable;

final class MindustryComponentStringBuilder implements ComponentStringBuilder {

    private final IntSeq colors = new IntSeq();
    private final StringBuilder builder = new StringBuilder();
    private final KeyContainer context;
    private final ComponentRendererProvider provider;

    MindustryComponentStringBuilder(final KeyContainer context, final ComponentRendererProvider provider) {
        this.context = context;
        this.provider = provider;
    }

    @Override
    public KeyContainer getContext() {
        return this.context;
    }

    @Override
    public ComponentStringBuilder append(final Component component) {
        {
            var color = component.getTextStyle().getTextColor();
            final var rgb = color == null ? -1 : color.getRGB();
            final var previous = this.colors.isEmpty() ? -1 : this.colors.peek();
            this.colors.add(rgb);
            if (rgb != -1 && rgb != previous) {
                this.builder.append("[#").append(String.format("%06X", rgb)).append(']');
            }
        }

        final var renderer = this.provider.getRenderer(component);
        if (renderer != null) renderer.render(component, this);

        {
            final var popped = this.colors.pop();
            final var previous = this.colors.isEmpty() ? -1 : this.colors.peek();
            if (popped != -1 && popped != previous) {
                this.builder.append("[]");
            }
        }

        return this;
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq) {
        return this.append(csq, 0, csq == null ? 0 : csq.length());
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq, final int start, final int end) {
        if (csq == null) {
            this.builder.append("null");
        } else {
            for (int i = start; i < end; i++) {
                this.append(csq.charAt(i));
            }
        }
        return this;
    }

    @Override
    public ComponentStringBuilder append(final char c) {
        if (c == '[') {
            this.builder.append("[[");
        } else {
            this.builder.append(c);
        }
        return this;
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
