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
@DistributorDataClass
@Value.Immutable
public interface KeyWithCType<T extends MappableContent> {

    KeyWithCType<Block> BLOCK =
            KeyWithCTypeImpl.of(Key.of(Key.MINDUSTRY_NAMESPACE, "block", Block.class), ContentType.block);

    KeyWithCType<UnitType> UNIT =
            KeyWithCTypeImpl.of(Key.of(Key.MINDUSTRY_NAMESPACE, "unit", UnitType.class), ContentType.unit);

    KeyWithCType<Item> ITEM =
            KeyWithCTypeImpl.of(Key.of(Key.MINDUSTRY_NAMESPACE, "item", Item.class), ContentType.item);

    KeyWithCType<Liquid> LIQUID =
            KeyWithCTypeImpl.of(Key.of(Key.MINDUSTRY_NAMESPACE, "liquid", Liquid.class), ContentType.liquid);

    KeyWithCType<Weather> WEATHER =
            KeyWithCTypeImpl.of(Key.of(Key.MINDUSTRY_NAMESPACE, "weather", Weather.class), ContentType.weather);

    KeyWithCType<StatusEffect> STATUS =
            KeyWithCTypeImpl.of(Key.of(Key.MINDUSTRY_NAMESPACE, "status", StatusEffect.class), ContentType.status);

    KeyWithCType<Planet> PLANET =
            KeyWithCTypeImpl.of(Key.of(Key.MINDUSTRY_NAMESPACE, "planet", Planet.class), ContentType.planet);

    List<KeyWithCType<?>> ALL = List.of(BLOCK, UNIT, ITEM, LIQUID, WEATHER, STATUS, PLANET);

    Key<T> getKey();

    ContentType getContentType();
}
