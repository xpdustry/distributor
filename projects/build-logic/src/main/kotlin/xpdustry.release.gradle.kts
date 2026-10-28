@file:Suppress("UnstableApiUsage")

// The release of the monorepo, applied to the root project, which lists the released plugins in release

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
