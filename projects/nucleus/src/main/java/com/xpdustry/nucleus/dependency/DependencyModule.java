// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.dependency;

public interface DependencyModule {

    void configure(DependencyService.Binder binder);
}
