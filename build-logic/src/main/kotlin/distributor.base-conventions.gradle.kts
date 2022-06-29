import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("net.kyori.indra")
    id("net.kyori.indra.checkstyle")
    id("net.ltgt.errorprone")
    id("com.github.johnrengelman.shadow")
}

indra {
    checkstyle("9.3")

    javaVersions {
        target(17)
        minimumToolchain(17)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.xpdustry.fr/releases") {
        name = "xpdustry-repository-releases"
        mavenContent { releasesOnly() }
    }
}

dependencies {
    val junit = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

    val jetbrains = "23.0.0"
    compileOnly("org.jetbrains:annotations:$jetbrains")
    testCompileOnly("org.jetbrains:annotations:$jetbrains")
    annotationProcessor("com.uber.nullaway:nullaway:0.9.4")
    errorprone("com.google.errorprone:error_prone_core:2.10.0")
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("MissingSummary", "BadImport")
        if (!name.contains("test", true)) {
            check("NullAway", CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "fr.xpdustry.distributor")
        }
    }
}

tasks.create("getArtifactPath") {
    doLast { println(tasks.shadowJar.get().archiveFile.get().toString()) }
}

tasks.shadowJar {
    from(rootProject.file("LICENSE.md")) {
        into("META-INF")
    }
}

tasks.build.get().dependsOn(tasks.shadowJar)
