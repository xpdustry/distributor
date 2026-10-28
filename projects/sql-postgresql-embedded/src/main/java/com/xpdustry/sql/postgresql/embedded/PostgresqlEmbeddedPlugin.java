// SPDX-License-Identifier: MIT
package com.xpdustry.sql.postgresql.embedded;

import arc.util.Log;
import mindustry.mod.Plugin;

/// Provides an embedded PostgreSQL server to the other plugins.
public final class PostgresqlEmbeddedPlugin extends Plugin {

    @Override
    public void init() {
        Log.info("Loaded the embedded PostgreSQL server");
    }
}
