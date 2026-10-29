plugins {
    alias(conventions.plugins.xpdustry.mindustry)
}

description = "Provides the PostgreSQL JDBC driver to the other plugins."

xpdustry {
    plugin {
        displayName = "SQL-PostgreSQL"
        mainClass = "com.xpdustry.sql.postgresql.PostgresqlPlugin"
    }
}

dependencies {
    api(libs.postgresql)
    // Its annotations are only declared for the runtime, which breaks compiling against it
    compileOnlyApi(libs.postgresql.annotations)
}
