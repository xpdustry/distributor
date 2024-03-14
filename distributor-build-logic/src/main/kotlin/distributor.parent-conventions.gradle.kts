import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare
import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

tasks.register<Copy>("dist") {
    dependsOn(tasks.build)
    from(rootProject.subprojects.filter { it.plugins.hasPlugin(ShadowJavaPlugin::class) }.map { it.tasks.named<ShadowJar>("shadowJar") })
    into(temporaryDir)
    rename { it.replace("-${rootProject.version}-plugin", "") }
}
