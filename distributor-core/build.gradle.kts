plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

dependencies {
    /*
    api("net.mindustry_ddns:file-store:1.3.0")
    api("com.google.code.gson:gson:2.9.0")
    api("org.aeonbits.owner:owner-java8:1.0.12")
     */

    // TODO I don't know what to do
    implementation("org.fusesource.jansi:jansi:2.4.0")
    api("io.leangen.geantyref:geantyref:1.3.13")

    val cloud = "1.7.0"
    api("cloud.commandframework:cloud-annotations:$cloud")
    api("cloud.commandframework:cloud-tasks:$cloud")
    api("cloud.commandframework:cloud-core:$cloud")
    api("cloud.commandframework:cloud-services:$cloud")
}
