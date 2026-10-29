plugins {
    alias(conventions.plugins.xpdustry.mindustry)
    alias(conventions.plugins.xpdustry.publishing)
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
    api(libs.postgresql.embedded)
    // The embedded server logs through slf4j 1.7, upgraded to the slf4j 2 of the other plugins, silenced like theirs
    runtimeOnly(libs.slf4j.nop)
}

configurations.runtimeClasspath {
    exclude(group = "org.postgresql")
}
