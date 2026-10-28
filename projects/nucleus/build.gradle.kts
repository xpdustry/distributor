plugins {
    alias(conventions.plugins.xpdustry.mindustry)
    alias(conventions.plugins.xpdustry.publishing)
}

description = "The main plugin of the xpdustry servers."

xpdustry {
    plugin {
        mainClass = "com.xpdustry.nucleus.NucleusPlugin"
    }
}

dependencies {
    mindustryImplementation(projects.foundation)
    mindustryRuntimeOnly(projects.sqlPostgresql)
    mindustryCompileOnly(projects.sqlPostgresqlEmbedded)
}
