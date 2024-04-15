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
package com.xpdustry.distributor.api.annotation.method;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

final class ResultBuilderImpl implements MethodAnnotationScanner.Result.Builder {

    private final Map<MethodAnnotationScanner.Key<?, ?>, List<?>> outputs = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <I extends Annotation, O> MethodAnnotationScanner.Result.Builder addOutput(
            final MethodAnnotationScanner.Key<I, O> key, final O output) {
        ((List<O>) this.outputs.computeIfAbsent(key, k -> new ArrayList<>())).add(output);
        return this;
    }

    @Override
    public MethodAnnotationScanner.Result build() {
        return ResultImpl.of(outputs);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "{", "}")
                .add("outputs=" + outputs)
                .toString();
    }
}
