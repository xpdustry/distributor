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
package com.xpdustry.distributor.api.command.cloud.parser;

import com.xpdustry.distributor.api.command.cloud.MindustryCaptionKeys;
import com.xpdustry.distributor.api.key.CTypeKey;
import java.util.Locale;
import mindustry.Vars;
import mindustry.ctype.MappableContent;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;

/**
 * A parser for {@link MappableContent content} arguments.
 * Will only succeed on exact matches.
 *
 * @param <C> the command sender type
 * @param <T> the content type
 */
public final class ContentParser<C, T extends MappableContent> implements ArgumentParser<C, T> {

    @SuppressWarnings("unchecked")
    public static <C, T extends MappableContent> ParserDescriptor<C, T> contentParser(final CTypeKey<T> contentType) {
        return ParserDescriptor.of(new ContentParser<>(contentType), (Class<T>)
                contentType.getKey().getToken().getRawType());
    }

    public static <C, T extends MappableContent> CommandComponent.Builder<C, T> contentComponent(
            final CTypeKey<T> contentType) {
        return CommandComponent.<C, T>builder().parser(contentParser(contentType));
    }

    private final CTypeKey<T> contentType;

    public ContentParser(final CTypeKey<T> contentType) {
        this.contentType = contentType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArgumentParseResult<T> parse(final CommandContext<C> ctx, final CommandInput input) {
        final var name = input.readString().toLowerCase(Locale.ROOT);
        final var content = Vars.content.getByName(this.contentType.getContentType(), name);
        return (content == null)
                ? ArgumentParseResult.failure(new ContentParseException(ctx, name, this.contentType))
                : ArgumentParseResult.success((T) content);
    }

    /**
     * An exception thrown when a content argument could not be parsed.
     */
    public static final class ContentParseException extends ParserException {

        private final String input;
        private final CTypeKey<?> contentType;

        /**
         * Creates a new {@link ContentParseException}.
         *
         * @param input       the input string
         * @param ctx         the command context
         * @param contentType the content type
         */
        public ContentParseException(final CommandContext<?> ctx, final String input, final CTypeKey<?> contentType) {
            super(
                    ContentParser.class,
                    ctx,
                    MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_CONTENT,
                    CaptionVariable.of("input", input),
                    CaptionVariable.of("type", contentType.getContentType().name()));
            this.input = input;
            this.contentType = contentType;
        }

        public String getInput() {
            return this.input;
        }

        public CTypeKey<?> getContentType() {
            return this.contentType;
        }
    }
}
