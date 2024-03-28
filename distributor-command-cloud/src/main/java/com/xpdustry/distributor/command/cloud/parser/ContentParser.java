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
package com.xpdustry.distributor.command.cloud.parser;

import com.xpdustry.distributor.command.cloud.ArcCaptionKeys;
import java.util.Locale;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;

public sealed class ContentParser<C, T extends MappableContent> implements ArgumentParser<C, T> {

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

    public static final class Weather<C> extends ContentParser<C, mindustry.type.Weather> {

        public static <C> ParserDescriptor<C, mindustry.type.Weather> weatherParser() {
            return ParserDescriptor.of(new Weather<>(), mindustry.type.Weather.class);
        }

        public static <C> CommandComponent.Builder<C, mindustry.type.Weather> weatherComponent() {
            return CommandComponent.<C, mindustry.type.Weather>builder().parser(weatherParser());
        }

        public Weather() {
            super(ContentType.weather);
        }
    }

    public static final class Unit<C> extends ContentParser<C, UnitType> {

        public static <C> ParserDescriptor<C, UnitType> unitParser() {
            return ParserDescriptor.of(new Unit<>(), UnitType.class);
        }

        public static <C> CommandComponent.Builder<C, UnitType> unitComponent() {
            return CommandComponent.<C, UnitType>builder().parser(unitParser());
        }

        public Unit() {
            super(ContentType.unit);
        }
    }

    public static final class Status<C> extends ContentParser<C, StatusEffect> {

        public static <C> ParserDescriptor<C, StatusEffect> statusParser() {
            return ParserDescriptor.of(new Status<>(), StatusEffect.class);
        }

        public static <C> CommandComponent.Builder<C, StatusEffect> statusComponent() {
            return CommandComponent.<C, StatusEffect>builder().parser(statusParser());
        }

        public Status() {
            super(ContentType.status);
        }
    }

    public static final class Planet<C> extends ContentParser<C, mindustry.type.Planet> {

        public static <C> ParserDescriptor<C, mindustry.type.Planet> planetParser() {
            return ParserDescriptor.of(new Planet<>(), mindustry.type.Planet.class);
        }

        public static <C> CommandComponent.Builder<C, mindustry.type.Planet> planetComponent() {
            return CommandComponent.<C, mindustry.type.Planet>builder().parser(planetParser());
        }

        public Planet() {
            super(ContentType.planet);
        }
    }

    public static final class Liquid<C> extends ContentParser<C, mindustry.type.Liquid> {

        public static <C> ParserDescriptor<C, mindustry.type.Liquid> liquidParser() {
            return ParserDescriptor.of(new Liquid<>(), mindustry.type.Liquid.class);
        }

        public static <C> CommandComponent.Builder<C, mindustry.type.Liquid> liquidComponent() {
            return CommandComponent.<C, mindustry.type.Liquid>builder().parser(liquidParser());
        }

        public Liquid() {
            super(ContentType.liquid);
        }
    }

    public static final class Item<C> extends ContentParser<C, mindustry.type.Item> {

        public static <C> ParserDescriptor<C, mindustry.type.Item> itemParser() {
            return ParserDescriptor.of(new Item<>(), mindustry.type.Item.class);
        }

        public static <C> CommandComponent.Builder<C, mindustry.type.Item> itemComponent() {
            return CommandComponent.<C, mindustry.type.Item>builder().parser(itemParser());
        }

        public Item() {
            super(ContentType.item);
        }
    }

    public static final class Block<C> extends ContentParser<C, mindustry.world.Block> {

        public static <C> ParserDescriptor<C, mindustry.world.Block> blockParser() {
            return ParserDescriptor.of(new Block<>(), mindustry.world.Block.class);
        }

        public static <C> CommandComponent.Builder<C, mindustry.world.Block> blockComponent() {
            return CommandComponent.<C, mindustry.world.Block>builder().parser(blockParser());
        }

        public Block() {
            super(ContentType.block);
        }
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
