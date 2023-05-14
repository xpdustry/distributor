import fr.xpdustry.toxopid.dsl.anukenJitpack
import fr.xpdustry.toxopid.dsl.mindustryDependencies

plugins {
    id("net.kyori.indra")
    id("com.github.johnrengelman.shadow")
    id("fr.xpdustry.toxopid")
}

val metadata = fr.xpdustry.toxopid.spec.ModMetadata.fromJson(rootProject.file("plugin.json"))

toxopid {
    compileVersion.set("v" + metadata.minGameVersion)
    platforms.add(fr.xpdustry.toxopid.spec.ModPlatform.HEADLESS)
    useMindustryMirror.set(true)
}

repositories {
    mavenCentral()
    anukenJitpack()
}

dependencies {
    mindustryDependencies()
}

tasks.runMindustryClient {
    mods.setFrom()
}

tasks.register("getArtifactPath") {
    doLast { println(tasks.shadowJar.get().archiveFile.get().toString()) }
}

tasks.shadowJar {
    archiveClassifier.set("plugin")
    from(rootProject.file("LICENSE.md")) {
        into("META-INF")
    }
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
