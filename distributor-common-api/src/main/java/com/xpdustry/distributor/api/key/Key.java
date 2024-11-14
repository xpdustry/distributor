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

import com.xpdustry.distributor.api.util.TypeToken;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.UUID;
import org.immutables.value.Value;

/**
 * A key is typesafe way of retrieving values from containers.
 *
 * @param <V> the type of the value.
 */
@DistributorDataClass
@Value.Immutable
public interface Key<V> {

    /**
     * The mindustry namespace. Used for keys originating from vanilla mindustry.
     */
    String MINDUSTRY_NAMESPACE = "mindustry";

    /**
     * The distributor namespace. Used for keys originating from distributor.
     */
    String DISTRIBUTOR_NAMESPACE = "distributor";

    /**
     * The generated namespace. Used for keys that are generated and do not need a specific name.
     */
    String GENERATED_NAMESPACE = "generated";

    static Key<Void> of(final String name) {
        ensureValidString(name, "name");
        return KeyImpl.of(MINDUSTRY_NAMESPACE, name, TypeToken.of(Void.class));
    }

    static Key<Void> of(final String namespace, final String name) {
        ensureValidString(namespace, "namespace");
        ensureValidString(name, "name");
        return KeyImpl.of(namespace, name, TypeToken.of(Void.class));
    }

    static <T> Key<T> of(final String name, final Class<T> type) {
        ensureValidString(name, "name");
        return KeyImpl.of(MINDUSTRY_NAMESPACE, name, TypeToken.of(type));
    }

    static <T> Key<T> of(final String name, final TypeToken<T> type) {
        ensureValidString(name, "name");
        return KeyImpl.of(MINDUSTRY_NAMESPACE, name, type);
    }

    static <T> Key<T> of(final String namespace, final String name, final TypeToken<T> type) {
        ensureValidString(namespace, "namespace");
        return KeyImpl.of(namespace, name, type);
    }

    static <T> Key<T> of(final String namespace, final String name, final Class<T> type) {
        ensureValidString(namespace, "namespace");
        return KeyImpl.of(namespace, name, TypeToken.of(type));
    }

    static <T> Key<T> generated(final Class<T> type) {
        return KeyImpl.of(GENERATED_NAMESPACE, UUID.randomUUID().toString(), TypeToken.of(type));
    }

    static <T> Key<T> generated(final TypeToken<T> type) {
        return KeyImpl.of(GENERATED_NAMESPACE, UUID.randomUUID().toString(), type);
    }

    private static void ensureValidString(final String value, final String name) {
        if (value.isBlank()) throw new IllegalArgumentException(name + " cannot be empty nor blank");
    }

    String getNamespace();

    String getName();

    @Value.Auxiliary
    TypeToken<V> getToken();
}
