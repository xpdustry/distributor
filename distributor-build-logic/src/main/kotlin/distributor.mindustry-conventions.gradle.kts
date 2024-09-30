import com.xpdustry.toxopid.spec.ModDependency
import com.xpdustry.toxopid.spec.ModMetadata
import com.xpdustry.toxopid.spec.ModPlatform
import com.xpdustry.toxopid.task.GithubAssetDownload

plugins {
    id("net.kyori.indra")
    id("com.gradleup.shadow")
    id("com.xpdustry.toxopid")
}

val extension = project.extensions.create<DistributorModuleExtension>(DistributorModuleExtension.EXTENSION_NAME)

toxopid {
    compileVersion = libs.versions.mindustry
    platforms = setOf(ModPlatform.SERVER)
}

dependencies {
    compileOnly(toxopid.dependencies.mindustryCore)
    compileOnly(toxopid.dependencies.arcCore)
    testImplementation(toxopid.dependencies.mindustryCore)
    testImplementation(toxopid.dependencies.arcCore)
}

val downloadSlf4md by tasks.registering(GithubAssetDownload::class) {
    owner = "xpdustry"
    repo = "slf4md"
    asset = "slf4md-simple.jar"
    version = libs.versions.slf4md.map { "v$it" }
}

tasks.runMindustryServer {
    mods.from(downloadSlf4md)
}

val generateMetadataFile by tasks.registering {
    val output = temporaryDir.resolve("plugin.json")
    outputs.file(output)
    doLast {
        output.writeText(
            ModMetadata.toJson(
                ModMetadata(
                    name = extension.identifier.get(),
                    displayName = extension.display.get(),
                    description = extension.description.get(),
                    version = project.version.toString(),
                    repository = "xpdustry/distributor",
                    author = "xpdustry",
                    minGameVersion = libs.versions.mindustry.get().substring(1),
                    mainClass = extension.main.get(),
                    java = true,
                    hidden = true,
                    dependencies = (extension.dependencies.get() + "slf4md").mapTo(mutableListOf(), ::ModDependency),
                ),
            ),
        )
    }
}

tasks.shadowJar {
    archiveClassifier.set("plugin")
    from(generateMetadataFile)
    from(rootProject.file("LICENSE.md")) { into("META-INF") }
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
