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
package com.xpdustry.distributor.common.component.codec;

import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.component.style.ComponentStyle;
import com.xpdustry.distributor.api.key.Key;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MindustryEncoderImpl extends AbstractStringComponentEncoder {

    @Override
    public Key<Void> getKey() {
        return MINDUSTRY_ENCODER;
    }

    @Override
    protected Appender createAppender(final @Nullable ComponentStyle previous) {
        final var appender = new MindustryAppender();
        if (previous != null) {
            final var color = previous.getTextColor();
            if (color != null) {
                appender.colors.push(color);
            }
        }
        return appender;
    }

    static final class MindustryAppender implements Appender {

        private final Deque<ComponentColor> colors = new ArrayDeque<>();
        private final StringBuilder builder = new StringBuilder();

        @SuppressWarnings("NullAway")
        @Override
        public void appendStyleStart(final ComponentStyle style) {
            var color = style.getTextColor();
            if (color == null) {
                color = colors.peek();
            }
            if (color != null) {
                if (!Objects.equals(color, colors.peek())) {
                    builder.append("[#")
                            .append(String.format("%06X", color.getRGB()))
                            .append(']');
                }
                colors.push(color);
            }
        }

        @Override
        public void appendText(final String text) {
            builder.append(text.replace("[", "[["));
        }

        @Override
        public void appendRawText(final String text) {
            builder.append(text);
        }

        @Override
        public void appendStyleClose() {
            final var current = colors.poll();
            if (!Objects.equals(current, colors.peek())) {
                builder.append("[]");
            }
        }

        @Override
        public String toString() {
            return builder.toString();
        }

        @Override
        public ComponentStyle getCurrentStyle() {
            final var color = colors.peek();
            return color == null ? ComponentStyle.empty() : ComponentStyle.style(color);
        }
    }
}
