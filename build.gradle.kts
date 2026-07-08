import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.xpdustry.toxopid.ToxopidExtension
import com.xpdustry.toxopid.extension.anukeXpdustry
import com.xpdustry.toxopid.spec.ModDependency
import com.xpdustry.toxopid.spec.ModMetadata
import com.xpdustry.toxopid.spec.ModPlatform
import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec
import net.kyori.indra.IndraExtension
import net.kyori.indra.git.task.RequireClean
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("com.diffplug.spotless") version "8.7.0"
    id("net.kyori.indra") version "4.0.0"
    id("net.kyori.indra.publishing") version "4.0.0"
    id("com.gradleup.shadow") version "9.4.1"
    id("com.xpdustry.toxopid") version "4.2.0"
    id("net.ltgt.errorprone") version "5.1.0"
}

group = "com.xpdustry"
version = file("VERSION.txt").readText().trim() + if (findProperty("is_release").toString().toBoolean()) "" else "-SNAPSHOT"
description = "The next-generation core API for Mindustry plugins."

val metadata =
    ModMetadata(
        name = "foundation",
        displayName = "Foundation",
        description = description!!,
        author = "Xpdustry",
        version = version.toString(),
        mainClass = "com.xpdustry.foundation.FoundationPlugin",
        repository = "xpdustry/distributor",
        java = true,
        hidden = true,
        minGameVersion = "159",
        dependencies = mutableListOf(ModDependency("slf4md", soft = true)),
    )

repositories {
    mavenCentral()
    anukeXpdustry()
}

val toxopid = extensions.getByType<ToxopidExtension>()
toxopid.platforms = setOf(ModPlatform.SERVER)
toxopid.compileVersion = "v${metadata.minGameVersion}"

dependencies {
    compileOnly(toxopid.dependencies.mindustryCore)
    testImplementation(toxopid.dependencies.mindustryCore)
    compileOnly(toxopid.dependencies.arcCore)
    testImplementation(toxopid.dependencies.arcCore)
    compileOnly(toxopid.dependencies.mindustryHeadless)
    testImplementation(toxopid.dependencies.mindustryHeadless)

    compileOnlyApi("org.slf4j:slf4j-api:2.0.18")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.18")

    compileOnlyApi("org.jspecify:jspecify:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.junit.vintage:junit-vintage-engine:6.0.1")
    testImplementation("com.google.guava:guava-testlib:33.4.8-jre")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    errorprone("com.google.errorprone:error_prone_core:2.49.0")
    errorprone("com.uber.nullaway:nullaway:0.13.4")
}

configurations.runtimeClasspath {
    exclude(group = "org.slf4j")
    exclude(group = "com.google.errorprone")
}

configure<IndraExtension> {
    javaVersions {
        target(25)
        minimumToolchain(25)
    }

    publishSnapshotsTo("xpdustry", "https://maven.xpdustry.com/snapshots")
    publishReleasesTo("xpdustry", "https://maven.xpdustry.com/releases")

    gpl3OnlyLicense()

    github("xpdustry", "distributor") {
        ci(true)
        issues(true)
        scm(true)
    }

    configurePublications {
        pom {
            organization {
                name = "xpdustry"
                url = "https://www.xpdustry.com"
            }

            developers {
                developer {
                    id.set("Phinner")
                    timezone.set("Europe/Brussels")
                }
            }
        }
    }
}

configure<SpotlessExtension> {
    java {
        palantirJavaFormat("2.94.0")
        formatAnnotations()
        importOrder("", "\\#")
        forbidModuleImports()
        forbidWildcardImports()
        licenseHeader("// SPDX-License-Identifier: GPL-3.0-only")
    }
    kotlinGradle {
        ktlint()
    }
}

signing {
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?
    useInMemoryPgpKeys(signingKey, signingPassword)
}

tasks.withType<RequireClean> {
    enabled = false
}

tasks.withType<JavaCompile> {
    options.errorprone {
        disableWarningsInGeneratedCode = true
        disable("MissingSummary", "InlineMeSuggester")
        option("NullAway:OnlyNullMarked")
        option("NullAway:JSpecifyMode", "true")
        check("NullAway", CheckSeverity.ERROR)
    }
}

val generateMetadataFile =
    tasks.register("generateMetadataFile") {
        inputs.property("metadata", metadata)
        val output = temporaryDir.resolve("plugin.json")
        outputs.file(output)
        doLast { output.writeText(ModMetadata.toJson(metadata)) }
    }

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName = "${project.name}.jar"
    archiveClassifier = "plugin"
    from(rootProject.file("LICENSE.md")) { into("META-INF") }
    mergeServiceFiles()
    from(generateMetadataFile)
    minimize()
}

tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME) {
    dependsOn(tasks.named<ShadowJar>("shadowJar"))
}

val downloadSlf4md =
    tasks.register<GithubAssetDownload>("downloadSlf4md") {
        owner = "xpdustry"
        repo = "slf4md"
        asset = "slf4md.jar"
        version = "v1.3.0"
    }

tasks.named<MindustryExec>(MindustryExec.SERVER_EXEC_TASK_NAME) {
    mods.from(downloadSlf4md)
}

tasks.withType<MindustryExec> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
