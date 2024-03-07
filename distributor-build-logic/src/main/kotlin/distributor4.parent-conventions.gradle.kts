import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare

plugins {
    id("com.diffplug.spotless")
    id("net.kyori.indra.git")
    id("com.github.ben-manes.versions")
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

// https://github.com/ben-manes/gradle-versions-plugin
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.dependencyUpdates {
    // https://github.com/ben-manes/gradle-versions-plugin
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
