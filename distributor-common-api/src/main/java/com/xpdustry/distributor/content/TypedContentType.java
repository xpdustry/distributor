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
package com.xpdustry.distributor.content;

import com.xpdustry.distributor.internal.DistributorDataClass;
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

@DistributorDataClass
@Value.Immutable
public sealed interface TypedContentType<T extends MappableContent> permits TypedContentTypeImpl {

    TypedContentType<Block> BLOCK = TypedContentType.of(ContentType.block, Block.class);
    TypedContentType<UnitType> UNIT = TypedContentType.of(ContentType.unit, UnitType.class);
    TypedContentType<Item> ITEM = TypedContentType.of(ContentType.item, Item.class);
    TypedContentType<Liquid> LIQUID = TypedContentType.of(ContentType.liquid, Liquid.class);
    TypedContentType<Weather> WEATHER = TypedContentType.of(ContentType.weather, Weather.class);
    TypedContentType<StatusEffect> STATUS = TypedContentType.of(ContentType.status, StatusEffect.class);
    TypedContentType<Planet> PLANET = TypedContentType.of(ContentType.planet, Planet.class);
    List<TypedContentType<?>> ALL = List.of(BLOCK, UNIT, ITEM, LIQUID, WEATHER, STATUS, PLANET);

    // TODO Hide constructor to enforce type safety ?
    static <T extends MappableContent> TypedContentType<T> of(final ContentType contentType, final Class<T> clazz) {
        return TypedContentTypeImpl.of(contentType, clazz);
    }

    ContentType getContentType();

    Class<T> getContentTypeClass();
}
