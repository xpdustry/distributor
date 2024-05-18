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
import com.xpdustry.distributor.api.component.style.ComponentStyle;
import com.xpdustry.distributor.api.key.Key;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fusesource.jansi.Ansi;

// TODO Get rid of jansi, the natives are not needed and a pain in the ass to remove from the final jar
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

    @SuppressWarnings("unchecked")
    private static final class ConsoleAppender implements Appender {

        private static final Supplier<Ansi> ANSI_BUILDER;

        static {
            if ((OS.isWindows && !OS.hasEnv("WT_SESSION")) || OS.isAndroid) {
                try {
                    final var constructor = (Constructor<Ansi>)
                            Class.forName("org.fusesource.jansi.Ansi.NoAnsi").getDeclaredConstructor();
                    constructor.setAccessible(true);
                    ANSI_BUILDER = () -> {
                        try {
                            return constructor.newInstance();
                        } catch (final ReflectiveOperationException error) {
                            throw new IllegalStateException("Failed to create a no-op Ansi builder", error);
                        }
                    };
                } catch (final ReflectiveOperationException error) {
                    throw new IllegalStateException("Failed to find the no-args constructor for Ansi", error);
                }
            } else {
                ANSI_BUILDER = Ansi::ansi;
            }
        }

        private final Deque<ComponentStyle> styles = new ArrayDeque<>();
        private final Ansi builder = ANSI_BUILDER.get();

        @Override
        public void appendStyleStart(final ComponentStyle style) {
            var previous = styles.peek();
            if (previous == null) {
                styles.push(style);
            } else {
                styles.push(merge(previous, style));
            }

            if (Objects.equals(previous, styles.getLast())) {
                return;
            }

            appendStyle(style);
        }

        @Override
        public void appendText(final String text) {
            builder.a(text);
        }

        @Override
        public void appendRawText(final String text) {
            builder.a(text);
        }

        @Override
        public void appendStyleClose() {
            final var current = styles.pop();
            final var previous = styles.peek();
            if (previous == null) {
                builder.reset();
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
            builder.reset();
            final var textColor = style.getTextColor();
            if (textColor != null) {
                builder.fgRgb(textColor.getRGB());
            }
            final var backColor = style.getBackColor();
            if (backColor != null) {
                builder.bgRgb(backColor.getRGB());
            }
            for (final var entry : style.getDecorations().entrySet()) {
                final var decoration = entry.getKey();
                if (!entry.getValue()) continue;
                switch (decoration) {
                    case BOLD -> builder.a(Ansi.Attribute.INTENSITY_BOLD);
                    case STRIKETHROUGH -> builder.a(Ansi.Attribute.STRIKETHROUGH_ON);
                    case UNDERLINED -> builder.a(Ansi.Attribute.UNDERLINE);
                    case ITALIC -> builder.a(Ansi.Attribute.ITALIC);
                }
            }
        }

        private ComponentStyle merge(final ComponentStyle previous, final ComponentStyle next) {
            final var builder = previous.toBuilder();
            final var textColor = next.getTextColor();
            if (textColor != null) builder.setTextColor(textColor);
            final var backColor = next.getBackColor();
            if (backColor != null) builder.setBackColor(backColor);
            final var decorations = previous.getDecorations();
            decorations.putAll(next.getDecorations());
            builder.setDecorations(decorations);
            return builder.build();
        }
    }
}
