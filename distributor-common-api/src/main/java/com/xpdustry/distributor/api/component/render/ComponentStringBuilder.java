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

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.key.KeyContainer;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A component string builder.
 */
public interface ComponentStringBuilder extends Appendable {

    /**
     * Returns a plain component string builder.
     * Will not apply any text styles or colors.
     *
     * @param context  the context
     * @param provider the component renderer provider
     * @return the plain component string builder
     */
    static ComponentStringBuilder plain(final KeyContainer context, final ComponentRendererProvider provider) {
        return new PlainComponentStringBuilder(context, provider);
    }

    /**
     * Returns a plain component string builder.
     * Will not apply any text styles or colors.
     * <p>
     * Uses the component renderer provided by {@link com.xpdustry.distributor.api.Distributor}.
     *
     * @param context the context
     * @return the plain component string builder
     */
    static ComponentStringBuilder plain(final KeyContainer context) {
        return new PlainComponentStringBuilder(context, Distributor.get().getComponentRendererProvider());
    }

    /**
     * Returns an ANSI component string builder.
     * Will apply ANSI escape codes for text styles and colors.
     * <p>
     * If a valid ANSI escape code is found in an appended text, it will be removed.
     *
     * @param context  the context
     * @param provider the component renderer provider
     * @return the ANSI component string builder
     */
    static ComponentStringBuilder ansi(final KeyContainer context, final ComponentRendererProvider provider) {
        return new AnsiComponentStringBuilder(context, provider);
    }

    /**
     * Returns an ANSI component string builder.
     * Will apply ANSI escape codes for text styles and colors.
     * <p>
     * If a valid ANSI escape code is found in an appended text, it will be removed.
     * <p>
     * Uses the component renderer provided by {@link com.xpdustry.distributor.api.Distributor}.
     *
     * @param context the context
     * @return the ANSI component string builder
     */
    static ComponentStringBuilder ansi(final KeyContainer context) {
        return new AnsiComponentStringBuilder(context, Distributor.get().getComponentRendererProvider());
    }

    /**
     * Returns a Mindustry component string builder.
     * Will apply Mindustry-specific text colors.
     * <p>
     * If an open square bracket is found in an appended text, it will be replaced with a double square bracket.
     *
     * @param context  the context
     * @param provider the component renderer provider
     * @return the Mindustry component string builder
     */
    static ComponentStringBuilder mindustry(final KeyContainer context, final ComponentRendererProvider provider) {
        return new MindustryComponentStringBuilder(context, provider);
    }

    /**
     * Returns a Mindustry component string builder.
     * Will apply Mindustry-specific text colors.
     * <p>
     * If an open square bracket is found in an appended text, it will be replaced with a double square bracket.
     * <p>
     * Uses the component renderer provided by {@link com.xpdustry.distributor.api.Distributor}.
     *
     * @param context the context
     * @return the Mindustry component string builder
     */
    static ComponentStringBuilder mindustry(final KeyContainer context) {
        return new MindustryComponentStringBuilder(context, Distributor.get().getComponentRendererProvider());
    }

    /**
     * Returns the context of this builder.
     */
    KeyContainer getContext();

    /**
     * Appends the specified component.
     *
     * @param component the component
     * @return this builder
     */
    ComponentStringBuilder append(final Component component);

    @Override
    ComponentStringBuilder append(final @Nullable CharSequence csq);

    @Override
    ComponentStringBuilder append(final @Nullable CharSequence csq, final int start, final int end);

    @Override
    ComponentStringBuilder append(final char c);

    /**
     * Returns the rendered string.
     */
    @Override
    String toString();
}
