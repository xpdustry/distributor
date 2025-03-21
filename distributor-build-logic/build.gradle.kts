plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version libs.versions.spotless
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.toxopid)
    implementation(libs.spotless)
    implementation(libs.shadow)
    implementation(libs.bundles.indra)
    implementation(libs.errorprone.gradle)

    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

spotless {
    kotlin {
        targetExclude("src/*/kotlin/**.gradle.kts", "build/generated-sources/**")
        ktlint().editorConfigOverride(mapOf("max_line_length" to "120", "ktlint_standard_filename" to "disabled"))
    }
    kotlinGradle {
        target("*.gradle.kts", "src/*/kotlin/**.gradle.kts")
        ktlint().editorConfigOverride(mapOf("max_line_length" to "120"))
    }
}
