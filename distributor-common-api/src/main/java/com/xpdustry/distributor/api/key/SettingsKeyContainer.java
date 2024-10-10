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

import arc.Core;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mindustry.io.JsonIO;
import org.checkerframework.checker.nullness.qual.Nullable;

enum SettingsKeyContainer implements MutableKeyContainer {
    INSTANCE;

    {
        setSerializerIfAbsent(Instant.class, new InstantSerializer());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> getOptional(final Key<V> key) {
        final var name = toInternalKey(key);
        return Optional.ofNullable((V) Core.settings.get(name, Core.settings.getDefault(name)));
    }

    @Override
    public boolean contains(final Key<?> key) {
        return Core.settings.has(toInternalKey(key));
    }

    @Override
    public Set<Key<?>> getKeys() {
        final var keys = new HashSet<Key<?>>();
        for (final var key : Core.settings.keys()) keys.add(fromInternalKey(key));
        return keys;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> @Nullable V set(final Key<V> key, final V value) {
        if (!isSupportedType(value.getClass())) {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
        final var name = toInternalKey(key);
        final var previous = Core.settings.get(name, null);
        Core.settings.put(name, value);
        return (V) previous;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> @Nullable V remove(final Key<V> key) {
        final var name = toInternalKey(key);
        final var previous = Core.settings.get(name, null);
        Core.settings.remove(name);
        return (V) previous;
    }

    private boolean isSupportedType(final Class<?> clazz) {
        return JsonIO.json.getSerializer(clazz) != null;
    }

    private <T> void setSerializerIfAbsent(final Class<T> clazz, final Json.Serializer<T> serializer) {
        if (JsonIO.json.getSerializer(clazz) == null) {
            JsonIO.json.setSerializer(clazz, serializer);
        }
    }

    private String toInternalKey(final Key<?> key) {
        return key.getNamespace().equals(Key.MINDUSTRY_NAMESPACE)
                ? key.getName()
                : key.getNamespace() + ":" + key.getName();
    }

    private Key<Void> fromInternalKey(final String key) {
        final var index = key.indexOf(':');
        return index == -1 ? Key.of(key) : Key.of(key.substring(0, index), key.substring(index + 1));
    }

    @SuppressWarnings("rawtypes")
    private static final class InstantSerializer implements Json.Serializer<Instant> {

        @Override
        public void write(final Json json, final Instant object, final Class knownType) {
            json.writeValue(object.toEpochMilli());
        }

        @Override
        public Instant read(final Json json, final JsonValue jsonData, final Class type) {
            return Instant.ofEpochMilli(jsonData.asLong());
        }
    }
}
