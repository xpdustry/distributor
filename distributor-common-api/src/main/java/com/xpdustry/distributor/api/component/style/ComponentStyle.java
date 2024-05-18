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
package com.xpdustry.distributor.api.component.style;

import com.xpdustry.distributor.api.permission.TriState;
import com.xpdustry.distributor.api.util.Buildable;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@DistributorDataClass
@Value.Immutable
public interface ComponentStyle extends Buildable<ComponentStyle, ComponentStyle.Builder> {

    static ComponentStyle style(
            final @Nullable ComponentColor textColor,
            final @Nullable ComponentColor backColor,
            final Map<TextDecoration, Boolean> decorations) {
        return ComponentStyleImpl.of(textColor, backColor, decorations);
    }

    static ComponentStyle style(final @Nullable ComponentColor textColor, final @Nullable ComponentColor backColor) {
        return ComponentStyleImpl.of(textColor, backColor, Map.of());
    }

    static ComponentStyle style(final @Nullable ComponentColor textColor) {
        return ComponentStyleImpl.of(textColor, null, Map.of());
    }

    static ComponentStyle style(final @Nullable ComponentColor textColor, final TextDecoration... decorations) {
        return ComponentStyleImpl.of(
                textColor,
                null,
                Arrays.stream(decorations).collect(Collectors.toMap(Function.identity(), decoration -> true)));
    }

    static ComponentStyle style(final Map<TextDecoration, Boolean> decorations) {
        return ComponentStyleImpl.of(null, null, decorations);
    }

    static ComponentStyle empty() {
        return ComponentStyleBuilderImpl.EMPTY;
    }

    static ComponentStyle.Builder builder() {
        return new ComponentStyleBuilderImpl();
    }

    @Nullable ComponentColor getTextColor();

    @Nullable ComponentColor getBackColor();

    Map<TextDecoration, Boolean> getDecorations();

    @Override
    default Builder toBuilder() {
        return new ComponentStyleBuilderImpl()
                .setTextColor(getTextColor())
                .setBackColor(getBackColor())
                .setDecorations(getDecorations());
    }

    default ComponentStyle merge(final ComponentStyle override) {
        final var builder = toBuilder();
        if (override.getTextColor() != null) {
            builder.setTextColor(override.getTextColor());
        }
        if (override.getBackColor() != null) {
            builder.setBackColor(override.getBackColor());
        }
        builder.setDecorations(override.getDecorations());
        return builder.build();
    }

    interface Builder extends Setter<Builder>, Buildable.Builder<ComponentStyle, Builder> {}

    interface Setter<T extends Setter<T>> {

        T setTextColor(final @Nullable ComponentColor textColor);

        T setBackColor(final @Nullable ComponentColor backColor);

        default T addDecoration(final TextDecoration decoration) {
            return setDecoration(decoration, TriState.TRUE);
        }

        T setDecorations(final Map<TextDecoration, ? extends Boolean> decorations);

        T setDecoration(final TextDecoration decoration, final TriState state);
    }
}
