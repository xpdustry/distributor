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

import com.xpdustry.distributor.api.component.style.ComponentStyle;
import com.xpdustry.distributor.api.key.Key;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlainTextEncoderImpl extends AbstractStringComponentEncoder {

    @Override
    public Key getKey() {
        return PLAINTEXT_ENCODER;
    }

    @Override
    protected Appender createAppender(@Nullable ComponentStyle previous) {
        return new PlainTextAppender();
    }

    private static final class PlainTextAppender implements Appender {

        private final StringBuilder builder = new StringBuilder();

        @Override
        public void appendText(final String text) {
            builder.append(text);
        }

        @Override
        public void appendRawText(final String text) {
            builder.append(text);
        }

        @Override
        public void appendStyleStart(final ComponentStyle style) {}

        @Override
        public void appendStyleClose() {}

        @Override
        public ComponentStyle getCurrentStyle() {
            return ComponentStyle.empty();
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
