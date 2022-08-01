plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
    api("io.leangen.geantyref:geantyref:1.3.13")

    val configurate = "4.1.2"
    implementation("org.spongepowered:configurate-gson:$configurate")
    implementation("org.spongepowered:configurate-yaml:$configurate")
    implementation("org.spongepowered:configurate-hocon:$configurate")

    val cloud = "1.7.0"
    api("cloud.commandframework:cloud-annotations:$cloud")
    api("cloud.commandframework:cloud-tasks:$cloud")
    api("cloud.commandframework:cloud-core:$cloud")
    api("cloud.commandframework:cloud-services:$cloud")
}
