@file:Suppress("UnstableApiUsage")

val release = configurations.dependencyScope("release")
val releaseClasspath = configurations.resolvable("releaseClasspath") {
    extendsFrom(release.get())
    usingMindustryPluginVariant(objects)
}

tasks.register<Sync>("collect") {
    description = "Collect the plugin jars of the release in build/release-artifacts."
    from(releaseClasspath)
    into(layout.buildDirectory.dir("release-artifacts"))
}
