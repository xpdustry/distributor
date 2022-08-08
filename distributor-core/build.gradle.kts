plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

tasks.downloadMindustryServer {
    artifacts.add(
        fr.xpdustry.toxopid.task.GitHubArtifact.release(
            "Anuken", "Mindustry", toxopid.runtimeVersion.get(), "server-release.jar"
        )
    )
}

tasks.downloadMindustryClient {
    artifacts.add(
        fr.xpdustry.toxopid.task.GitHubArtifact.release(
            "Anuken", "Mindustry", toxopid.runtimeVersion.get(), "Mindustry.jar"
        )
    )
}

dependencies {
    implementation("org.spongepowered:configurate-yaml:4.1.2")

    api("io.leangen.geantyref:geantyref:1.3.13")

    val examination = "1.3.0"
    api("net.kyori:examination-api:$examination")
    api("net.kyori:examination-string:$examination")

    val cloud = "1.7.0"
    api("cloud.commandframework:cloud-core:$cloud")
    api("cloud.commandframework:cloud-annotations:$cloud")
    api("cloud.commandframework:cloud-tasks:$cloud")
    api("cloud.commandframework:cloud-services:$cloud")
    annotationProcessor("cloud.commandframework:cloud-annotations:$cloud")
}
