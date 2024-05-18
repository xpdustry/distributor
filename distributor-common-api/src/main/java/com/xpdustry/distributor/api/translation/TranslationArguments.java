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
package com.xpdustry.distributor.api.translation;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import com.xpdustry.distributor.internal.annotation.DistributorDataClassSingleton;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

public sealed interface TranslationArguments {

    static TranslationArguments.Array array(final List<Object> values) {
        return ArrayImpl.of(values);
    }

    static TranslationArguments.Array array(final Object... values) {
        return ArrayImpl.of(List.of(values));
    }

    static TranslationArguments.Named named(final Map<String, Object> values) {
        return NamedImpl.of(values);
    }

    static TranslationArguments.Empty empty() {
        return EmptyImpl.of();
    }

    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Array extends TranslationArguments {

        List<Object> getArguments();
    }

    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Named extends TranslationArguments {

        Map<String, Object> getArguments();
    }

    @DistributorDataClassSingleton
    @Value.Immutable
    non-sealed interface Empty extends TranslationArguments {}
}
