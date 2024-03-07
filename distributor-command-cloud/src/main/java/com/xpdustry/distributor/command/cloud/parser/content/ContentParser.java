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
package com.xpdustry.distributor.command.cloud.parser.content;

import com.xpdustry.distributor.command.cloud.ArcCaptionKeys;
import java.util.Locale;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;

public sealed class ContentParser<C, T extends MappableContent> implements ArgumentParser<C, T>
        permits BlockParser, ItemParser, LiquidParser, PlanetParser, StatusParser, UnitParser, WeatherParser {

    private final ContentType contentType;

    ContentParser(final ContentType contentType) {
        this.contentType = contentType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArgumentParseResult<T> parse(final CommandContext<C> ctx, final CommandInput input) {
        final var name = input.readString().toLowerCase(Locale.ROOT);
        final var content = Vars.content.getByName(contentType, name);
        return (content == null)
                ? ArgumentParseResult.failure(new ContentParseException(this.getClass(), ctx, name, contentType))
                : ArgumentParseResult.success((T) content);
    }

    @SuppressWarnings("serial")
    public static final class ContentParseException extends ParserException {

        private final String input;
        private final ContentType contentType;

        @SuppressWarnings("rawtypes")
        public ContentParseException(
                final Class<? extends ContentParser> clazz,
                final CommandContext<?> ctx,
                final String input,
                final ContentType contentType) {
            super(
                    clazz,
                    ctx,
                    ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_CONTENT,
                    CaptionVariable.of("input", input),
                    CaptionVariable.of("type", contentType.name()));
            this.input = input;
            this.contentType = contentType;
        }

        public String getInput() {
            return input;
        }

        public ContentType getContentType() {
            return contentType;
        }
    }
}
