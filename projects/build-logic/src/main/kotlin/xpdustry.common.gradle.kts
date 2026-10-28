import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
}

val xpdustry = extensions.create<XpdustryExtension>("xpdustry")

dependencies {
    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
    errorprone(libs.errorprone.palantir)
    compileOnlyApi(libs.jspecify)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.junit.platform.launcher)
}

configurations.runtimeClasspath {
    exclude(group = "com.google.errorprone")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

spotless {
    java {
        // The default target includes the generated sources
        targetExclude("build/**")
        palantirJavaFormat()
        formatAnnotations()
        importOrder("", "\\#")
        forbidModuleImports()
        forbidWildcardImports()
        licenseHeader("// SPDX-License-Identifier: MIT")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-parameters", "-Xlint:all,-processing", "-Werror"))
    options.errorprone {
        disableWarningsInGeneratedCode = true
        excludedPaths = ".*/build/generated/.*"
        disable("MissingSummary", "InlineMeSuggester")
        // From the palantir checks, var is part of our style
        disable("VarUsage")
        // From the palantir checks, they push the palantir safe-logging library
        disable(
            "PreferSafeLoggableExceptions",
            "PreferSafeLogger",
            "PreferSafeLoggingPreconditions",
            "Slf4jLogsafeArgs",
        )
        option("NullAway:OnlyNullMarked")
        option("NullAway:JSpecifyMode", "true")
        check("NullAway", CheckSeverity.ERROR)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
