plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
}

dependencies {
    api("net.mindustry_ddns:file-store:1.3.0")
    api("com.google.code.gson:gson:2.9.0")
    api("org.aeonbits.owner:owner-java8:1.0.12")

    val cloud = "1.6.1"
    api("cloud.commandframework:cloud-annotations:$cloud")
    api("cloud.commandframework:cloud-tasks:$cloud")
    api("cloud.commandframework:cloud-core:$cloud")
}
