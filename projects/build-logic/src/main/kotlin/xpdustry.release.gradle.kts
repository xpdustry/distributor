@file:Suppress("UnstableApiUsage")

import java.time.Instant

plugins {
    alias(conventions.plugins.xpdustry.maven)
}

val release = configurations.dependencyScope("release")
val releaseClasspath = configurations.resolvable("releaseClasspath") {
    extendsFrom(release.get())
    usingMindustryPluginVariant(objects)
}

tasks.register<Sync>("collect") {
    description = "Collect the plugin jars of the release in build/release-artifacts."
    from(releaseClasspath)
    into(layout.buildDirectory.dir("release-artifacts"))
}

val mindustryServer = configurations.dependencyScope("mindustryServer") {
    dependencies.addLater(libs.mindustry.release.server)
}
val mindustryServerClasspath = configurations.resolvable("mindustryServerClasspath") {
    extendsFrom(mindustryServer.get())
}

// Fails without any commit or outside a git repository
val head = providers.exec {
    commandLine("git", "show", "--no-patch", "--format=%H %ct", "HEAD")
    isIgnoreExitValue = true
}.standardOutput.asText.map { it.trim().split(' ').takeIf { parts -> parts.size == 2 } ?: listOf("", "0") }

val generateDistributionManifest = tasks.register<GenerateDistributionManifest>("generateDistributionManifest") {
    description = "Generate the distribution manifest, the files a server runs for this build."
    plugins.from(releaseClasspath)
    mindustryServer.fileProvider(mindustryServerClasspath.map { it.singleFile })
    mindustryVersion = libs.versions.mindustry
    // Where the mindustry-releases repository of the settings downloads it from
    mindustryUrl = libs.versions.mindustry.map {
        "https://github.com/Anuken/Mindustry/releases/download/$it/server-release.jar"
    }
    distributionVersion = version.toString()
    commit = head.map { it[0] }
    committed = head.map { Instant.ofEpochSecond(it[1].toLong()).toString() }
    java = JAVA_VERSION
    output = layout.buildDirectory.file("distribution/monorepo-distribution.json")
}

publishing {
    publications.register<MavenPublication>("distribution") {
        artifactId = "monorepo-distribution"
        artifact(generateDistributionManifest) { extension = "json" }
        pom {
            name = artifactId
            description = "The files a server runs for a build of the xpdustry monorepo."
            packaging = "json"
        }
    }
}

// Servers update as soon as the manifest is published, so every plugin it lists must be published first
val self = path
tasks.named("publishDistributionPublicationToXpdustryRepository") {
    dependsOn(
        releaseClasspath.map { classpath ->
            classpath.incoming.resolutionResult.allComponents
                .mapNotNull { (it.id as? ProjectComponentIdentifier)?.projectPath }
                .filter { it != self }
                .map { "$it:publishMavenPublicationToXpdustryRepository" }
        },
    )
}
