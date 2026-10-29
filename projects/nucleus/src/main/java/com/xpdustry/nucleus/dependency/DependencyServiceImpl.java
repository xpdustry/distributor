// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.dependency;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.Collectors;

final class DependencyServiceImpl implements DependencyService {

    private final Map<Key<?>, Factory<?>> factories = new LinkedHashMap<>();
    private final SequencedMap<Key<?>, Object> instances = new LinkedHashMap<>();

    public DependencyServiceImpl(final DependencyModule... modules) {
        final var binder = new BinderImpl();
        for (final var module : modules) {
            module.configure(binder);
        }
        binder.bind(DependencyService.class, this);
    }

    @Override
    public <T> T instantiate(final Class<T> type) {
        return this.instantiate(getInjectableConstructor(type), new LinkedHashSet<>());
    }

    private <T> T instantiate(final Constructor<T> constructor, final SequencedSet<Key<?>> visited) {
        final var arguments = getInjectionKeys(constructor).stream()
                .map(key -> this.resolve(key, visited))
                .toArray();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke " + constructor, e);
        }
    }

    @Override
    public List<Object> resolveAll() {
        final var visited = new LinkedHashSet<Key<?>>();
        for (final var key : this.factories.keySet()) {
            final var _ = this.resolve(key, visited);
        }
        return new ArrayList<>(this.instances.sequencedValues());
    }

    @Override
    public <T> T resolve(final Class<T> type) {
        return this.resolve(new Key<>(type), new LinkedHashSet<>());
    }

    private <T> T resolve(final Key<T> key, final SequencedSet<Key<?>> visited) {
        if (this.instances.containsKey(key)) {
            return key.type.cast(this.instances.get(key));
        }
        final var factory = this.factories.get(key);
        if (factory == null) {
            throw new IllegalStateException("No bindings found for " + key);
        }
        try {
            if (!visited.add(key)) {
                throw new IllegalStateException("Circular bindings detected: "
                        + visited.stream().map(Key::toString).collect(Collectors.joining(" -> ")));
            }
            final var value = factory.create(visited);
            this.instances.put(key, value);
            return key.type.cast(value);
        } finally {
            visited.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getInjectableConstructor(final Class<T> type) {
        final var constructors = Arrays.stream(type.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .toList();
        return switch (constructors.size()) {
            case 0 -> throw new IllegalArgumentException(type.getName() + " has no injectable constructor");
            case 1 -> (Constructor<T>) constructors.getFirst();
            default -> throw new IllegalArgumentException(type.getName() + " has more than one injectable annotation");
        };
    }

    private static List<Key<?>> getInjectionKeys(final Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameters())
                .<Key<?>>map(parameter -> {
                    try {
                        return new Key<>(getInjectionKeyType(parameter.getParameterizedType()));
                    } catch (final Exception e) {
                        throw new IllegalArgumentException(
                                "Failed to create injection key for parameter " + parameter.getName() + " in "
                                        + constructor,
                                e);
                    }
                })
                .toList();
    }

    private static Class<?> getInjectionKeyType(final Type type) {
        if (!(type instanceof Class<?> clazz)) {
            throw new IllegalArgumentException(type.getTypeName() + " is a generic type");
        }
        final var generics = clazz.getTypeParameters();
        if (generics.length != 0) {
            throw new IllegalArgumentException(type.getTypeName() + " has generic type parameters");
        }
        return clazz;
    }

    private record Key<T>(Class<T> type) {
        @Override
        public String toString() {
            return this.type.getSimpleName();
        }
    }

    private sealed interface Factory<T> {

        T create(SequencedSet<Key<?>> visited);
    }

    private final class ConstructorFactory<T> implements Factory<T> {

        private final Constructor<T> constructor;

        private ConstructorFactory(final Constructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T create(final SequencedSet<Key<?>> visited) {
            return DependencyServiceImpl.this.instantiate(this.constructor, visited);
        }
    }

    private record StaticInstanceFactory<T>(T instance) implements Factory<T> {

        @Override
        public T create(final SequencedSet<Key<?>> visited) {
            return this.instance;
        }
    }

    final class BinderImpl implements Binder {

        @Override
        public <T> void bind(final Class<T> type, final T instance) {
            final var _ = getInjectionKeyType(type);
            DependencyServiceImpl.this.factories.put(new Key<>(type), new StaticInstanceFactory<>(instance));
        }

        @Override
        public <T> void bind(final Class<T> type, final Class<? extends T> impl) {
            final var _ = getInjectionKeyType(type);
            final var _ = getInjectionKeyType(impl);
            DependencyServiceImpl.this.factories.put(
                    new Key<>(type), new ConstructorFactory<>(getInjectableConstructor(impl)));
        }
    }
}
