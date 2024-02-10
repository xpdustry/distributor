import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare

plugins {
    id("com.diffplug.spotless")
    id("net.kyori.indra.git")
}

repositories {
    mavenCentral()
}

spotless {
    predeclareDeps()
}

extensions.configure<SpotlessExtensionPredeclare> {
    java { palantirJavaFormat(libs.versions.palantir.get()) }
    kotlin { ktlint(libs.versions.ktlint.get()) }
    kotlinGradle { ktlint(libs.versions.ktlint.get()) }
}
