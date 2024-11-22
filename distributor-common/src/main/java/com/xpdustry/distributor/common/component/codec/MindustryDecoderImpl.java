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

import arc.graphics.Color;
import arc.graphics.Colors;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.ListComponent;
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.component.codec.ComponentDecoder;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.component.style.TextStyle;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MindustryDecoderImpl implements ComponentDecoder<String> {

    @Override
    public Component decode(final String input) {
        return decode(input, new IndexHolder(), null).compress();
    }

    private Component decode(final String input, final IndexHolder holder, final @Nullable ComponentColor previous) {
        final var list = ListComponent.components();
        list.setTextStyle(TextStyle.of(previous));
        while (holder.index < input.length()) {
            final var start = input.indexOf('[', holder.index);
            if (start == -1) {
                list.append(TextComponent.text(input.substring(holder.index)));
                holder.index = input.length();
                continue;
            }

            if (start + 1 < input.length() && input.charAt(start + 1) == '[') {
                list.append(TextComponent.text("["));
                holder.index = start + 2;
                continue;
            }

            final var close = input.indexOf(']', start);
            if (close == -1) {
                list.append(TextComponent.text(input.substring(start)));
                holder.index = input.length();
                continue;
            }

            final var value = input.substring(start + 1, close);
            if (value.isEmpty()) {
                if (holder.index < start) {
                    list.append(TextComponent.text(input.substring(holder.index, start)));
                }
                holder.index = close + 1;
                break;
            }

            var color = Colors.get(value);
            if (color == null && value.charAt(0) == '#') {
                color = tryParseHex(value.substring(1));
            }

            if (color != null) {
                if (holder.index < start) {
                    list.append(TextComponent.text(input.substring(holder.index, start)));
                }
                holder.index = close + 1;
                list.append(decode(input, holder, ComponentColor.from(color)));
            } else {
                list.append(TextComponent.text("[" + value + "]"));
                holder.index = close + 1;
            }
        }

        return list.build();
    }

    private @Nullable Color tryParseHex(final String value) {
        // Mindustry won't parse a color with a length of 7
        if (value.length() > 8 || value.length() == 7) {
            return null;
        }
        int shifts = Math.min(value.length(), 6);
        int color = 0;
        for (int i = 0; i < shifts; i++) {
            char ch = value.charAt(i);
            if (ch >= '0' && ch <= '9') color = color * 16 + (ch - '0');
            else if (ch >= 'a' && ch <= 'f') color = color * 16 + (ch - ('a' - 10));
            else if (ch >= 'A' && ch <= 'F') color = color * 16 + (ch - ('A' - 10));
            else return null;
        }
        for (int i = shifts; i < 6; i++) {
            color <<= 4;
        }
        return new Color().rgb888(color);
    }

    private static final class IndexHolder {
        int index = 0;
    }
}
