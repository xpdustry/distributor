plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

dependencies {
    api("org.slf4j:slf4j-api:2.0.3")
    implementation("org.aeonbits.owner:owner-java8:1.0.12")

    val geantyref = "1.3.13"
    api("io.leangen.geantyref:geantyref:$geantyref")

    val cloud = "1.7.1"
    api("cloud.commandframework:cloud-core:$cloud") {
        exclude("org.checkerframework", "checker-qual")
    }
    api("cloud.commandframework:cloud-annotations:$cloud") {
        exclude("org.checkerframework", "checker-qual")
    }
    api("cloud.commandframework:cloud-tasks:$cloud") {
        exclude("org.checkerframework", "checker-qual")
    }
    api("cloud.commandframework:cloud-services:$cloud") {
        exclude("org.checkerframework", "checker-qual")
    }
    annotationProcessor("cloud.commandframework:cloud-annotations:$cloud")
    compileOnly("org.jetbrains:annotations:23.0.0")
}
