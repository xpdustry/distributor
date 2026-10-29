// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.database;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class DatabaseModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(PostgresDatabase.class, PostgresDatabaseImpl.class);
    }
}
