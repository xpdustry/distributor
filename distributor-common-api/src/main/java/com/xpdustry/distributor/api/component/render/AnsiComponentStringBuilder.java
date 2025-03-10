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
import org.jspecify.annotations.Nullable;

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
        return this.context;
    }

    @Override
    public ComponentStringBuilder append(final Component component) {
        this.escape();

        {
            final var previous = this.styles.peek();
            final var next = previous == null ? component.getTextStyle() : previous.merge(component.getTextStyle());
            this.styles.push(next);
            if (!Objects.equals(previous, next)) {
                this.appendStyle(next);
            }
        }

        final var renderer = this.provider.getRenderer(component);
        if (renderer != null) renderer.render(component, this);

        {
            final var popped = this.styles.pop();
            final var head = this.styles.peek();
            if (!Objects.equals(popped, head)) {
                this.appendStyle(head == null ? TextStyle.of() : head);
            }
        }

        return this;
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq) {
        this.builder.append(csq);
        return this;
    }

    @Override
    public ComponentStringBuilder append(final @Nullable CharSequence csq, final int start, final int end) {
        this.builder.append(csq, start, end);
        return this;
    }

    @Override
    public ComponentStringBuilder append(final char c) {
        this.builder.append(c);
        return this;
    }

    @Override
    public String toString() {
        this.escape();
        return this.builder.toString();
    }

    private void escape() {
        this.escape(this.builder.length());
    }

    private void escape(final int end) {
        if (!ANSI_SUPPORTED || this.builder.length() == this.mark) return;
        final var matcher = ANSI_PATTERN.matcher(this.builder.substring(this.mark, end));
        this.builder.delete(this.mark, end);
        final var insert = new StringBuilder();
        while (matcher.find()) matcher.appendReplacement(insert, "");
        matcher.appendTail(insert);
        this.builder.insert(this.mark, insert);
        this.mark = this.builder.length();
    }

    private void appendStyle(final TextStyle style) {
        final var start = this.builder.length();
        this.appendReset();
        final var textColor = style.getTextColor();
        if (textColor != null) {
            this.appendForegroundColor(textColor);
        }
        final var backColor = style.getBackColor();
        if (backColor != null) {
            this.appendBackgroundColor(backColor);
        }
        for (final var entry : style.getDecorations().entrySet()) {
            final var decoration = entry.getKey();
            if (!entry.getValue()) continue;
            this.appendDecoration(decoration);
        }
        this.escape(start);
    }

    private void appendReset() {
        if (!ANSI_SUPPORTED) return;
        this.builder.append(ANSI_ESCAPE_START).append(0).append(ANSI_ESCAPE_CLOSE);
    }

    private void appendDecoration(final TextDecoration decoration) {
        if (!ANSI_SUPPORTED) return;
        this.builder.append(ANSI_ESCAPE_START);
        this.builder.append(
                switch (decoration) {
                    case BOLD -> 1;
                    case ITALIC -> 3;
                    case UNDERLINED -> 4;
                    case STRIKETHROUGH -> 9;
                });
        this.builder.append(ANSI_ESCAPE_CLOSE);
    }

    private void appendForegroundColor(final ComponentColor color) {
        if (!ANSI_SUPPORTED) return;
        this.builder
                .append(ANSI_ESCAPE_START)
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
        this.builder
                .append(ANSI_ESCAPE_START)
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
