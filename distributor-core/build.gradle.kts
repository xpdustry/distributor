plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

dependencies {
    api(project(":distributor-api"))
    api("org.aeonbits.owner:owner-java8:1.0.12")
    api("com.zaxxer:HikariCP:5.0.1")
    api("com.mysql:mysql-connector-j:8.0.32")
    api("com.password4j:password4j:1.7.0")
    api("org.slf4j:jul-to-slf4j:2.0.7")
    testImplementation("org.xerial:sqlite-jdbc:3.40.0.0")
}

val metadata = fr.xpdustry.toxopid.spec.ModMetadata.fromJson(rootProject.file("plugin.json"))
metadata.version = rootProject.version.toString()
metadata.description = rootProject.description.toString()
metadata.name = "distributor-core"
metadata.displayName = "DistributorCore"
metadata.main = "fr.xpdustry.distributor.core.DistributorCorePlugin"

tasks.shadowJar {
    archiveFileName.set("DistributorCore.jar")

    doFirst {
        val temp = temporaryDir.resolve("plugin.json")
        temp.writeText(metadata.toJson(true))
        from(temp)
    }

    minimize {
        exclude(dependency("fr.xpdustry:distributor-.*:.*"))
        exclude(dependency("cloud.commandframework:cloud-.*:.*"))
        exclude(dependency("org.slf4j:slf4j-api:.*"))
        exclude(dependency("io.leangen.geantyref:geantyref:.*"))
        exclude(dependency("com.mysql:mysql-connector-j:.*"))
    }

    val shadowPackage = "fr.xpdustry.distributor.core.shadow"
    relocate("org.aeonbits.owner", "$shadowPackage.owner")
    relocate("com.zaxxer.hikari", "$shadowPackage.hikari")
    relocate("com.mysql", "$shadowPackage.mysql")
    relocate("com.google.protobuf", "$shadowPackage.protobuf")
    relocate("com.password4j", "$shadowPackage.password4j")
    relocate("google", "$shadowPackage.google")
}
