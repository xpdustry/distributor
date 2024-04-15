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
package com.xpdustry.distributor.api.content;

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

@DistributorDataClass
@Value.Immutable
public sealed interface ContentTypeKey<T extends MappableContent> permits ContentTypeKeyImpl {

    ContentTypeKey<Block> BLOCK = ContentTypeKeyImpl.of(ContentType.block, Block.class);
    ContentTypeKey<UnitType> UNIT = ContentTypeKeyImpl.of(ContentType.unit, UnitType.class);
    ContentTypeKey<Item> ITEM = ContentTypeKeyImpl.of(ContentType.item, Item.class);
    ContentTypeKey<Liquid> LIQUID = ContentTypeKeyImpl.of(ContentType.liquid, Liquid.class);
    ContentTypeKey<Weather> WEATHER = ContentTypeKeyImpl.of(ContentType.weather, Weather.class);
    ContentTypeKey<StatusEffect> STATUS = ContentTypeKeyImpl.of(ContentType.status, StatusEffect.class);
    ContentTypeKey<Planet> PLANET = ContentTypeKeyImpl.of(ContentType.planet, Planet.class);
    List<ContentTypeKey<?>> ALL = List.of(BLOCK, UNIT, ITEM, LIQUID, WEATHER, STATUS, PLANET);

    ContentType getContentType();

    Class<T> getContentTypeClass();
}
