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

    implementation(libs.jackson.databind)
    implementation(libs.hikari)
    implementation(libs.bouncycastle.openpgp)
    // HikariCP logs through slf4j, unlike nucleus, which logs through foundation
    runtimeOnly(libs.slf4j.nop)

    compileOnly(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    compileOnly(libs.nullaway.annotations)
}

tasks.shadowJar {
    // HikariCP and slf4j load their classes by name, so they are kept whole
    minimize {
        exclude(dependency(libs.hikari.get()))
        exclude(dependency(libs.slf4j.nop.get()))
    }
    // Lists the JCA providers of BouncyCastle, which the minimization drops as the updater never uses them
    exclude("META-INF/services/java.security.Provider")
}

tasks.processResources {
    from(layout.settingsDirectory.file("KEYS.asc")) { into("com/xpdustry/nucleus/update") }
}
