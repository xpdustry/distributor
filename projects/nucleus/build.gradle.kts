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
    mindustryImplementation(projects.sqlPostgresql)
    mindustryCompileOnly(projects.sqlPostgresqlEmbedded)

    implementation(libs.gson)
    implementation(libs.hikari)
    // HikariCP logs through slf4j, unlike nucleus, which logs through foundation
    runtimeOnly(libs.slf4j.nop)

    compileOnly(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    compileOnly(libs.nullaway.annotations)
}
