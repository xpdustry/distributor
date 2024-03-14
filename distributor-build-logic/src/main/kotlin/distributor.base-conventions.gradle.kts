import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("com.diffplug.spotless")
    id("net.kyori.indra")
    id("net.kyori.indra.licenser.spotless")
    id("net.ltgt.errorprone")
}

version = rootProject.version
group = rootProject.group
description = rootProject.description

repositories {
    mavenCentral()
    maven("https://maven.xpdustry.com/mindustry") {
        name = "xpdustry-mindustry"
        mavenContent { releasesOnly() }
    }
}

dependencies {
    compileOnlyApi(libs.jspecify)
    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
    testImplementation(libs.bundles.tests)
}

indra {
    javaVersions {
        target(libs.versions.java.get().toInt())
        minimumToolchain(libs.versions.java.get().toInt())
    }
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("LICENSE_HEADER.md"))
}

spotless {
    java {
        palantirJavaFormat(libs.versions.palantir.get())
        formatAnnotations()
        importOrder("", "\\#")
        custom("noWildcardImports") {
            if (it.contains("*;\n")) {
                throw Error("No wildcard imports allowed")
            }
            it
        }
        bumpThisNumberIfACustomStepChanges(1)
    }
}

tasks.withType<JavaCompile> {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable(
            "MissingSummary",
            "BadImport",
            "FutureReturnValueIgnored",
            "InlineMeSuggester",
            "EmptyCatch",
        )
        if (!name.contains("test", true)) {
            check("NullAway", CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "com.xpdustry.distributor")
        }
    }
}
