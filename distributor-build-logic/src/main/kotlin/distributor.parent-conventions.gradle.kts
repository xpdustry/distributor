import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare
import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

tasks.register<Copy>("release") {
    dependsOn(tasks.build)
    from(
        rootProject.subprojects.filter {
            it.plugins.hasPlugin(ShadowJavaPlugin::class)
        }.map { it.tasks.named<ShadowJar>(ShadowJavaPlugin.SHADOW_JAR_TASK_NAME) },
    )
    into(temporaryDir)
    rename { it.replace("-${rootProject.version}-plugin", "") }
}
