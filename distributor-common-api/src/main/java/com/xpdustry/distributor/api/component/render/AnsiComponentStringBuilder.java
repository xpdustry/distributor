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

import arc.util.OS;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.component.style.TextDecoration;
import com.xpdustry.distributor.api.component.style.TextStyle;
import com.xpdustry.distributor.api.key.KeyContainer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.Nullable;

final class AnsiComponentStringBuilder implements ComponentStringBuilder {

    private static final boolean ANSI_SUPPORTED = OS.isLinux || OS.isMac || (OS.isWindows && OS.hasEnv("WT_SESSION"));
    private static final String ANSI_ESCAPE_START = "\033[";
    private static final String ANSI_ESCAPE_CLOSE = "m";
    private static final Pattern ANSI_PATTERN = Pattern.compile("(\033\\[[\\d;]*[a-zA-Z])");

    private final Deque<TextStyle> styles = new ArrayDeque<>();
    private final StringBuilder builder = new StringBuilder();
    private final KeyContainer context;
    private final ComponentRendererProvider provider;
    private int mark = 0;

    AnsiComponentStringBuilder(final KeyContainer context, final ComponentRendererProvider provider) {
        this.context = context;
        this.provider = provider;
    }

    @Override
    public KeyContainer getContext() {
        return context;
    }

    @Override
    public ComponentStringBuilder append(final Component component) {
        escape();

        {
            final var previous = styles.peek();
            final var next = previous == null ? component.getTextStyle() : previous.merge(component.getTextStyle());
            styles.push(next);
            if (!Objects.equals(previous, next)) {
                appendStyle(next);
            }
        }

        final var renderer = provider.getRenderer(component);
        if (renderer != null) renderer.render(component, this);

        {
            final var popped = styles.pop();
            final var head = styles.peek();
            if (!Objects.equals(popped, head)) {
                appendStyle(head == null ? TextStyle.of() : head);
            }
        }

        return this;
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq) {
        builder.append(csq);
        return this;
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq, final int start, final int end) {
        builder.append(csq, start, end);
        return this;
    }

    @Override
    public ComponentStringBuilder append(final char c) {
        builder.append(c);
        return this;
    }

    @Override
    public String toString() {
        escape();
        return builder.toString();
    }

    private void escape() {
        escape(builder.length());
    }

    private void escape(final int end) {
        if (!ANSI_SUPPORTED || builder.length() == mark) return;
        final var matcher = ANSI_PATTERN.matcher(builder.substring(mark, end));
        builder.delete(mark, end);
        final var insert = new StringBuilder();
        while (matcher.find()) matcher.appendReplacement(insert, "");
        matcher.appendTail(insert);
        builder.insert(mark, insert);
        mark = builder.length();
    }

    private void appendStyle(final TextStyle style) {
        final var start = builder.length();
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
            appendDecoration(decoration);
        }
        escape(start);
    }

    private void appendReset() {
        if (!ANSI_SUPPORTED) return;
        builder.append(ANSI_ESCAPE_START).append(0).append(ANSI_ESCAPE_CLOSE);
    }

    private void appendDecoration(final TextDecoration decoration) {
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
