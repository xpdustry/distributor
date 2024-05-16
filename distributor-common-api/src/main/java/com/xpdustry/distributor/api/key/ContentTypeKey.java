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
package com.xpdustry.distributor.api.key;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.List;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.Planet;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.type.Weather;
import mindustry.world.Block;
import org.immutables.value.Value;

/**
 * Typesafe {@link ContentType} providing the concrete class of a content type.
 *
 * @param <T> the content type
 */
@SuppressWarnings("immutables:subtype")
@DistributorDataClass
@Value.Immutable
public interface ContentTypeKey<T extends MappableContent> extends TypedKey<T> {

    ContentTypeKey<Block> BLOCK = ContentTypeKeyImpl.of("block", MINDUSTRY_NAMESPACE, Block.class, ContentType.block);

    ContentTypeKey<UnitType> UNIT =
            ContentTypeKeyImpl.of("unit", MINDUSTRY_NAMESPACE, UnitType.class, ContentType.unit);

    ContentTypeKey<Item> ITEM = ContentTypeKeyImpl.of("item", MINDUSTRY_NAMESPACE, Item.class, ContentType.item);

    ContentTypeKey<Liquid> LIQUID =
            ContentTypeKeyImpl.of("liquid", MINDUSTRY_NAMESPACE, Liquid.class, ContentType.liquid);

    ContentTypeKey<Weather> WEATHER =
            ContentTypeKeyImpl.of("weather", MINDUSTRY_NAMESPACE, Weather.class, ContentType.weather);

    ContentTypeKey<StatusEffect> STATUS =
            ContentTypeKeyImpl.of("status", MINDUSTRY_NAMESPACE, StatusEffect.class, ContentType.status);

    ContentTypeKey<Planet> PLANET =
            ContentTypeKeyImpl.of("planet", MINDUSTRY_NAMESPACE, Planet.class, ContentType.planet);

    List<ContentTypeKey<?>> ALL = List.of(BLOCK, UNIT, ITEM, LIQUID, WEATHER, STATUS, PLANET);

    ContentType getContentType();
}
