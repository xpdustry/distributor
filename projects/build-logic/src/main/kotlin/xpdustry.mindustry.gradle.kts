@file:Suppress("UnstableApiUsage")

import groovy.json.JsonOutput

plugins {
    alias(conventions.plugins.xpdustry.common)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.mindustry.core)
    compileOnly(libs.mindustry.server)
    compileOnly(libs.arc.core)
    testImplementation(libs.mindustry.core)
    testImplementation(libs.mindustry.server)
    testImplementation(libs.arc.core)
}

val plugin = the<XpdustryExtension>().plugin

// The plugins this one depends on, which provide their classes at runtime, so they are never bundled
val mindustryImplementation = configurations.dependencyScope("mindustryImplementation") {
    description = "The plugins compiled against and required at runtime, the hard dependencies."
}
val mindustryCompileOnly = configurations.dependencyScope("mindustryCompileOnly") {
    description = "The plugins compiled against but optional at runtime, the soft dependencies."
}
val mindustryRuntimeOnly = configurations.dependencyScope("mindustryRuntimeOnly") {
    description = "The plugins only required at runtime, the hard dependencies not compiled against."
}

configurations.compileOnlyApi {
    extendsFrom(mindustryImplementation.get(), mindustryCompileOnly.get())
}
configurations.testImplementation {
    extendsFrom(mindustryImplementation.get(), mindustryCompileOnly.get())
}
configurations.testRuntimeOnly {
    extendsFrom(mindustryRuntimeOnly.get())
}

// The names of the plugins declared in the given scopes, named after their project
fun pluginNames(vararg scopes: NamedDomainObjectProvider<DependencyScopeConfiguration>) = provider {
    scopes.flatMap { scope -> scope.get().dependencies.map { it.name } }.toSet()
}

val hardDependencies = pluginNames(mindustryImplementation, mindustryRuntimeOnly)
val softDependencies = pluginNames(mindustryCompileOnly).zip(hardDependencies) { soft, hard -> soft - hard }
plugin.displayName.convention(project.name.split('-').joinToString("") { it.replaceFirstChar(Char::uppercase) })

val generateMetadataFile = tasks.register("generateMetadataFile") {
    description = "Generate the plugin.json file."
    val output = layout.buildDirectory.file("generated/metadata/plugin.json")
    val metadata = provider {
        mapOf(
            "name" to project.name,
            "displayName" to plugin.displayName.get(),
            "description" to project.description!!,
            "author" to "Xpdustry",
            "version" to project.version.toString(),
            "main" to plugin.mainClass.get(),
            "repo" to GITHUB_REPOSITORY,
            "minGameVersion" to libs.versions.mindustry.get().removePrefix("v").substringBefore('.'),
            "hidden" to true,
            "java" to true,
            "dependencies" to hardDependencies.get().sorted(),
            "softDependencies" to softDependencies.get().sorted(),
        )
    }
    inputs.property("metadata", metadata)
    outputs.file(output)

    doLast {
        output.get().asFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(metadata.get())))
    }
}

// The plugin jar is released on GitHub, Maven only gets the jar the other plugins compile against
shadow {
    addShadowVariantIntoJavaComponent = false
}

tasks.shadowJar {
    archiveFileName = "${project.name}.jar"
    archiveClassifier = "plugin"
    from(generateMetadataFile)
    from(layout.settingsDirectory.file("LICENSE.md")) { into("META-INF") }
    mergeServiceFiles()
    // The duplicates are excluded before reaching the transformers, so the merged files must be included
    filesMatching("META-INF/services/**") { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
    // Ensures the included duplicates are merged by the transformers
    failOnDuplicateEntries = true
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

// Exposes the plugin jar to the other projects, for the release and their local servers, with the plugins it requires
configurations.consumable("pluginJar") {
    extendsFrom(mindustryImplementation.get(), mindustryRuntimeOnly.get())
    outgoing.artifact(tasks.shadowJar)
    usingMindustryPluginVariant(objects)
}

// The plugins loaded alongside this one by runMindustryServer
val mindustryPluginsClasspath = configurations.resolvable("mindustryPluginsClasspath") {
    extendsFrom(mindustryImplementation.get(), mindustryCompileOnly.get(), mindustryRuntimeOnly.get())
    usingMindustryPluginVariant(objects)
}

// The release jars of the server and the client, the Maven artifacts lack the game assets
val mindustryServer = configurations.dependencyScope("mindustryServer") {
    dependencies.addLater(libs.mindustry.release.server)
}
val mindustryServerClasspath = configurations.resolvable("mindustryServerClasspath") {
    extendsFrom(mindustryServer.get())
}
val mindustryClient = configurations.dependencyScope("mindustryClient") {
    dependencies.addLater(libs.mindustry.release.client)
}
val mindustryClientClasspath = configurations.resolvable("mindustryClientClasspath") {
    extendsFrom(mindustryClient.get())
}

val mindustryServerDirectory = layout.buildDirectory.dir("mindustry-server")

val prepareMindustryServer = tasks.register<Sync>("prepareMindustryServer") {
    description = "Copy this plugin and the plugins it depends on into the local server."
    from(tasks.shadowJar, mindustryPluginsClasspath)
    into(mindustryServerDirectory.map { it.dir("config/mods") })
    // The data directories of the plugins
    preserve { include("*/**") }
}

tasks.register<JavaExec>("runMindustryServer") {
    description = "Run a local Mindustry server with this plugin and the plugins it depends on."
    dependsOn(prepareMindustryServer)
    classpath(mindustryServerClasspath)
    mainClass = "mindustry.server.ServerLauncher"
    workingDir(mindustryServerDirectory)
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runMindustryClient") {
    description = "Run a local Mindustry client, with its data in the build directory."
    classpath(mindustryClientClasspath)
    mainClass = "mindustry.desktop.DesktopLauncher"
    val directory = layout.buildDirectory.dir("mindustry-client").get().asFile
    workingDir(directory)
    environment("MINDUSTRY_DATA_DIR", directory)
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    doFirst { directory.mkdirs() }
}
