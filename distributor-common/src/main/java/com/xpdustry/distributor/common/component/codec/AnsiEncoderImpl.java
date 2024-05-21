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

import arc.util.OS;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.component.style.ComponentStyle;
import com.xpdustry.distributor.api.component.style.TextDecoration;
import com.xpdustry.distributor.api.key.Key;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class AnsiEncoderImpl extends AbstractStringComponentEncoder {

    @Override
    public Key getKey() {
        return ANSI_ENCODER;
    }

    @Override
    protected Appender createAppender(final @Nullable ComponentStyle previous) {
        final var appender = new ConsoleAppender();
        if (previous != null) appender.styles.push(previous);
        return appender;
    }

    private static final class ConsoleAppender implements Appender {

        private static final boolean ANSI_SUPPORTED =
                OS.isLinux || OS.isMac || (OS.isWindows && OS.hasEnv("WT_SESSION"));
        private static final String ANSI_ESCAPE_START = "\033[";
        private static final String ANSI_ESCAPE_CLOSE = "m";
        private static final String ANSI_ESCAPE_START_RAW = "\\033[";
        private final Deque<ComponentStyle> styles = new ArrayDeque<>();
        private final StringBuilder builder = new StringBuilder();

        @Override
        public void appendStyleStart(final ComponentStyle style) {
            var previous = styles.peek();
            var next = previous == null ? style : previous.merge(style);
            styles.push(next);
            if (Objects.equals(previous, next)) return;
            appendStyle(next);
        }

        @Override
        public void appendText(final String text) {
            builder.append(text);
        }

        @Override
        public void appendRawText(final String text) {
            builder.append(text.replace(ANSI_ESCAPE_START, ANSI_ESCAPE_START_RAW));
        }

        @Override
        public void appendStyleClose() {
            final var current = styles.pop();
            final var previous = styles.peek();
            if (previous == null) {
                appendReset();
            } else if (!Objects.equals(current, styles.peek())) {
                appendStyle(previous);
            }
        }

        @Override
        public String toString() {
            return builder.toString();
        }

        @Override
        public ComponentStyle getCurrentStyle() {
            final var style = styles.peek();
            return style == null ? ComponentStyle.empty() : style;
        }

        private void appendStyle(final ComponentStyle style) {
            appendReset();
            final var textColor = style.getTextColor();
            if (textColor != null) {
                appendForegroundColor(textColor);
            }
            final var backColor = style.getBackColor();
            if (backColor != null) {
                appendBackgroundColor(backColor);
            }
            for (final var entry : style.getDecorations().entrySet()) {
                final var decoration = entry.getKey();
                if (!entry.getValue()) continue;
                appendDecorations(decoration);
            }
        }

        private void appendReset() {
            if (!ANSI_SUPPORTED) return;
            builder.append(ANSI_ESCAPE_START).append(0).append(ANSI_ESCAPE_CLOSE);
        }

        private void appendDecorations(final TextDecoration decoration) {
            if (!ANSI_SUPPORTED) return;
            builder.append(ANSI_ESCAPE_START);
            builder.append(
                    switch (decoration) {
                        case BOLD -> 1;
                        case ITALIC -> 3;
                        case UNDERLINED -> 4;
                        case STRIKETHROUGH -> 9;
                    });
            builder.append(ANSI_ESCAPE_CLOSE);
        }

        private void appendForegroundColor(final ComponentColor color) {
            if (!ANSI_SUPPORTED) return;
            builder.append(ANSI_ESCAPE_START)
                    .append(38)
                    .append(';')
                    .append(2)
                    .append(';')
                    .append(color.getR())
                    .append(';')
                    .append(color.getG())
                    .append(';')
                    .append(color.getB())
                    .append(ANSI_ESCAPE_CLOSE);
        }

        private void appendBackgroundColor(final ComponentColor color) {
            if (!ANSI_SUPPORTED) return;
            builder.append(ANSI_ESCAPE_START)
                    .append(48)
                    .append(';')
                    .append(2)
                    .append(';')
                    .append(color.getR())
                    .append(';')
                    .append(color.getG())
                    .append(';')
                    .append(color.getB())
                    .append(ANSI_ESCAPE_CLOSE);
        }
    }
}
