// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.dependency;

import java.util.List;

public interface DependencyService {

    static DependencyService create(final DependencyModule... modules) {
        return new DependencyServiceImpl(modules);
    }

    <T> T instantiate(Class<T> type);

    List<Object> resolveAll();

    <T> T resolve(Class<T> type);

    interface Binder {

        <T> void bind(Class<T> type, T instance);

        <T> void bind(Class<T> type, Class<? extends T> impl);

        default <T> void bind(final Class<T> type) {
            this.bind(type, type);
        }
    }
}
