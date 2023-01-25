plugins {
    kotlin("jvm") version "1.8.0"
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
    id("org.jetbrains.dokka")
}

repositories {
    maven("https://maven.xpdustry.fr/releases") {
        name = "xpdustry-releases"
        mavenContent { releasesOnly() }
    }
}

dependencies {
    compileOnly("fr.xpdustry:kotlin-runtime:2.0.0-k.1.8.0")
    compileOnly(project(":distributor-core"))
    api(cloudCommandFramework("kotlin-extensions"))
}

val metadata = fr.xpdustry.toxopid.spec.ModMetadata.fromJson(rootProject.file("plugin.json"))
metadata.version = rootProject.version.toString()
metadata.description = rootProject.description.toString()
metadata.name = "distributor-kotlin"
metadata.displayName = "DistributorKotlin"
metadata.main = "fr.xpdustry.distributor.kotlin.DistributorKotlinPlugin"
metadata.dependencies += listOf("distributor-core", "kotlin-runtime")

kotlin {
    coreLibrariesVersion = "1.8.0"
    explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

spotless {
    kotlin {
        ktlint()
    }
}

tasks.shadowJar {
    archiveFileName.set("DistributorKotlin.jar")

    doFirst {
        val temp = temporaryDir.resolve("plugin.json")
        temp.writeText(metadata.toJson(true))
        from(temp)
    }
}

tasks.javadocJar {
    from(tasks.dokkaHtml)
}

tasks.runMindustryServer {
    mods.setFrom(project(":distributor-core").tasks.shadowJar, tasks.shadowJar)
}
