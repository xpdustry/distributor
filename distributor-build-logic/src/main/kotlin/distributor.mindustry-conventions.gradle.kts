import fr.xpdustry.toxopid.dsl.mindustryDependencies
import fr.xpdustry.toxopid.spec.ModMetadata
import fr.xpdustry.toxopid.task.GithubArtifactDownload

plugins {
    id("net.kyori.indra")
    id("com.github.johnrengelman.shadow")
    id("fr.xpdustry.toxopid")
}

val extension = project.extensions.create<DistributorModuleExtension>(DistributorModuleExtension.EXTENSION_NAME)

toxopid {
    compileVersion.set(libs.versions.mindustry)
    platforms.add(fr.xpdustry.toxopid.spec.ModPlatform.HEADLESS)
}

dependencies {
    mindustryDependencies()
}

tasks.runMindustryClient {
    mods.setFrom()
}

val downloadSlf4md by tasks.registering(GithubArtifactDownload::class) {
    user = "xpdustry"
    repo = "slf4md"
    name = "slf4md-simple.jar"
    version = libs.versions.slf4md.map { "v$it" }
}

tasks.runMindustryServer {
    mods.from(tasks.shadowJar, downloadSlf4md)
}

tasks.shadowJar {
    doFirst {
        val metadata =
            ModMetadata(
                name = extension.identifier.get(),
                displayName = extension.display.get(),
                description = extension.description.get(),
                version = project.version.toString(),
                repo = "xpdustry/distributor",
                author = "xpdustry",
                minGameVersion = libs.versions.mindustry.get().substring(1),
                main = extension.main.get(),
                java = true,
                hidden = true,
                dependencies = extension.dependencies.get().toMutableList().apply { add("slf4md") },
            )

        val temp = temporaryDir.resolve("plugin.json")
        temp.writeText(metadata.toJson(true))
        from(temp)
    }

    archiveClassifier.set("plugin")
    from(rootProject.file("LICENSE.md")) {
        into("META-INF")
    }
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
