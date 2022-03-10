import fr.xpdustry.toxopid.extension.MindustryRepository
import fr.xpdustry.toxopid.extension.ModTarget
import groovy.json.JsonBuilder
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("net.kyori.indra")
    id("fr.xpdustry.toxopid")
    id("net.ltgt.errorprone")
    id("net.kyori.indra.git")
    id("net.kyori.indra.checkstyle")
}

val parentMetadata = readJson(file("$rootDir/global-plugin.json"))

indra {
    checkstyle("9.3")

    javaVersions {
        target(17)
        minimumToolchain(17)
    }
}

toxopid {
    modTarget.set(ModTarget.HEADLESS)
    val compileVersion = parentMetadata["minGameVersion"] as String
    arcCompileVersion.set(compileVersion)
    mindustryCompileVersion.set(compileVersion)

    mindustryRepository.set(MindustryRepository.BE)
    mindustryRuntimeVersion.set("22343")
}

repositories {
    mavenCentral()
    maven("https://repo.xpdustry.fr/releases") {
        name = "xpdustry-releases"
        mavenContent { releasesOnly() }
    }
}

dependencies {
    val junit = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

    val jetbrains = "23.0.0"
    compileOnly("org.jetbrains:annotations:$jetbrains")
    testCompileOnly("org.jetbrains:annotations:$jetbrains")
    annotationProcessor("com.uber.nullaway:nullaway:0.9.4")
    errorprone("com.google.errorprone:error_prone_core:2.10.0")
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("MissingSummary", "BadImport")
        if (!name.contains("test", true)) {
            check("NullAway", CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "fr.xpdustry.distributor")
        }
    }
}

tasks.create("getArtifactPath") {
    doLast { println((tasks.shadowJar.get() as Jar).archiveFile.get().toString()) }
}

tasks.named<Jar>("shadowJar") {
    val file = temporaryDir.resolve("plugin.json")
    val localMetadata = readJson(file("$projectDir/local-plugin.json"))
    file.writeText(JsonBuilder(localMetadata + parentMetadata).toPrettyString())
    from(file)
}
