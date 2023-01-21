plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

dependencies {
    implementation(project(":distributor-api"))
    implementation("org.aeonbits.owner:owner-java8:1.0.12")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mysql:mysql-connector-j:8.0.31")
    // MySQL driver has a vulnerability, so explicitly exclude it
    runtimeOnly("com.google.protobuf:protobuf-java:3.21.12")
    testImplementation("org.xerial:sqlite-jdbc:3.40.0.0")
}

val metadata = fr.xpdustry.toxopid.spec.ModMetadata.fromJson(rootProject.file("plugin.json"))
metadata.version = rootProject.version.toString()
metadata.description = rootProject.description.toString()
metadata.name = "distributor-core"
metadata.displayName = "Distributor"
metadata.main = "fr.xpdustry.distributor.core.DistributorPlugin"

tasks.shadowJar {
    archiveFileName.set("Distributor.jar")
    archiveClassifier.set("plugin")

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

    from(rootProject.file("LICENSE.md")) {
        into("META-INF")
    }
}

tasks.register("getArtifactPath") {
    doLast { println(tasks.shadowJar.get().archiveFile.get().toString()) }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
