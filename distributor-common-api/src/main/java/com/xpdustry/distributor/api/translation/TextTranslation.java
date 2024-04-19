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
import java.util.Map;
import org.immutables.value.Value;

@DistributorDataClass
@Value.Immutable
public sealed interface TextTranslation extends Translation permits TextTranslationImpl {

    static TextTranslation of(final String text) {
        return TextTranslationImpl.of(text);
    }

    String getText();

    @Override
    default String formatArray(final Object... args) {
        return this.getText();
    }

    @Override
    default String formatNamed(final Map<String, Object> args) {
        return this.getText();
    }

    @Override
    default String formatEmpty() {
        return this.getText();
    }
}