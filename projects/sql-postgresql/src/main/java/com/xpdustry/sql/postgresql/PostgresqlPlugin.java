// SPDX-License-Identifier: MIT
package com.xpdustry.sql.postgresql;

import arc.util.Log;
import mindustry.mod.Plugin;

/// Provides the PostgreSQL JDBC driver to the other plugins.
public final class PostgresqlPlugin extends Plugin {

    @Override
    public void init() {
        Log.info("Loaded the PostgreSQL JDBC driver");
    }
}
