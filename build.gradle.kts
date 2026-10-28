@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.spotless)
    alias(conventions.plugins.xpdustry.release)
}

description = "The monorepo of xpdustry."

tasks.register("printVersion") {
    description = "Print the project version."
    val version = version.toString()
    doLast { println(version) }
}

dependencies {
    release(projects.foundation)
    release(projects.nucleus)
    release(projects.sqlPostgresql)
    release(projects.sqlPostgresqlEmbedded)
}

spotless {
    kotlin {
        target("projects/build-logic/src/**/*.kt")
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) }
    }
    kotlinGradle {
        target("*.gradle.kts", "*/**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint()
            .editorConfigOverride(mapOf("ktlint_code_style" to "intellij_idea", "max_line_length" to "120"))
    }
}

spotlessPredeclare {
    java { palantirJavaFormat() }
    kotlin { ktfmt() }
    kotlinGradle { ktlint() }
}
