import fr.xpdustry.toxopid.task.GithubArtifactDownload

plugins {
    kotlin("jvm") version "1.9.23"
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

repositories {
    maven("https://maven.xpdustry.com/releases") {
        name = "xpdustry-releases"
        mavenContent { releasesOnly() }
    }
}

dependencies {
    compileOnly("com.xpdustry:kotlin-runtime:3.1.0-k.1.9.10")
    compileOnly(project(":distributor-core"))
    api(cloudCommandFramework("kotlin-extensions"))
    api(cloudCommandFramework("kotlin-coroutines"))
    api(cloudCommandFramework("kotlin-coroutines-annotations"))
    testImplementation(kotlin("stdlib"))
}

configurations.runtimeClasspath {
    exclude(group = "cloud.commandframework", module = "cloud-core")
    exclude(group = "cloud.commandframework", module = "cloud-annotations")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
}

val metadata = fr.xpdustry.toxopid.spec.ModMetadata.fromJson(rootProject.file("plugin.json"))
metadata.version = rootProject.version.toString()
metadata.description = rootProject.description.toString()
metadata.name = "distributor-kotlin"
metadata.displayName = "DistributorKotlin"
metadata.main = "fr.xpdustry.distributor.kotlin.DistributorKotlinPlugin"
metadata.dependencies += listOf("distributor-core", "kotlin-runtime")

kotlin {
    coreLibrariesVersion = "1.9.10"
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
    archiveFileName.set("distributor-kotlin.jar")

    doFirst {
        val temp = temporaryDir.resolve("plugin.json")
        temp.writeText(metadata.toJson(true))
        from(temp)
    }
}

val kotlinRuntime =
    tasks.register<GithubArtifactDownload>("downloadKotlinRuntime") {
        user.set("xpdustry")
        repo.set("kotlin-runtime")
        name.set("kotlin-runtime.jar")
        version.set("v3.1.0-k.1.9.10")
    }

tasks.runMindustryServer {
    mods.setFrom(project(":distributor-core").tasks.shadowJar, tasks.shadowJar, kotlinRuntime)
}

// Indra adds the javadoc task, we don't want that so disable it
components.named("java") {
    val component = this as AdhocComponentWithVariants
    component.withVariantsFromConfiguration(configurations.javadocElements.get()) {
        skip()
    }
}
