plugins {
    alias(conventions.plugins.xpdustry.mindustry)
}

description = "Provides an embedded PostgreSQL server to the other plugins."

xpdustry {
    plugin {
        displayName = "SQL-PostgreSQL-Embedded"
        mainClass = "com.xpdustry.sql.postgresql.embedded.PostgresqlEmbeddedPlugin"
    }
}

dependencies {
    mindustryRuntimeOnly(projects.sqlPostgresql)
    runtimeOnly(libs.postgresql.embedded)
}

// The driver itself is provided by sql-postgresql
configurations.runtimeClasspath {
    exclude(group = "org.postgresql")
}
