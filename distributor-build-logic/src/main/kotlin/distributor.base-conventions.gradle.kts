import com.diffplug.spotless.FormatterFunc
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import java.io.Serializable

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
    maven("https://jitpack.io") {
        name = "jitpack"
        mavenContent { releasesOnly() }
    }
}

dependencies {
    compileOnlyApi(libs.jspecify)
    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
    testImplementation(libs.bundles.test.lib)
    testRuntimeOnly(libs.bundles.test.engine)
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

class NoWildcardImports :
    FormatterFunc,
    Serializable {
    override fun apply(input: String) = if (input.contains("*;\n")) error("No wildcard imports allowed") else input
}

spotless {
    java {
        palantirJavaFormat(libs.versions.palantir.get())
        formatAnnotations()
        importOrder("", "\\#")
        custom("no-wildcard-imports", NoWildcardImports())
        bumpThisNumberIfACustomStepChanges(1)
    }
    kotlinGradle {
        target("*.gradle.kts", "src/*/kotlin/**.gradle.kts")
        ktlint(libs.versions.ktlint.get()).editorConfigOverride(mapOf("max_line_length" to "120"))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:-serial,-processing")
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("FutureReturnValueIgnored", "InlineMeSuggester", "EmptyCatch")
        check("NullAway", if (name.contains("test", ignoreCase = true)) CheckSeverity.OFF else CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", "com.xpdustry.distributor")
    }
}
