plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

fun DependencyHandler.cloudCommandFramework(module: String) {
    api("cloud.commandframework:cloud-$module:${Versions.cloud}")
}

dependencies {
    val apiGuardian = "1.1.2"
    compileOnlyApi("org.apiguardian:apiguardian-api:$apiGuardian")

    val slf4j = "2.0.3"
    api("org.slf4j:slf4j-api:$slf4j")

    val geantyref = "1.3.13"
    api("io.leangen.geantyref:geantyref:$geantyref")

    cloudCommandFramework("core")
    cloudCommandFramework("annotations")
    cloudCommandFramework("tasks")
    cloudCommandFramework("services")
}
