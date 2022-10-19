plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

val cloud = "1.7.1"

fun DependencyHandler.cloudCommandFramework(module: String) {
    api("cloud.commandframework:cloud-$module:$cloud") {
        exclude("org.checkerframework", "checker-qual")
        exclude("org.apiguardian", "apiguardian-api")
    }
}

dependencies {
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.aeonbits.owner:owner-java8:1.0.12")

    val slf4j = "2.0.3"
    api("org.slf4j:slf4j-api:$slf4j")
    testImplementation("org.slf4j:slf4j-simple:$slf4j")

    val geantyref = "1.3.13"
    api("io.leangen.geantyref:geantyref:$geantyref")

    cloudCommandFramework("core")
    cloudCommandFramework("annotations")
    cloudCommandFramework("tasks")
    cloudCommandFramework("services")
    annotationProcessor("cloud.commandframework:cloud-annotations:$cloud")

    // Temporary compile time artifacts until PRs are merged
    compileOnly("org.apiguardian:apiguardian-api:1.1.2")
    compileOnly("org.checkerframework:checker-qual:3.26.0")
}
