pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("build-logic")
}

fun includeSub(name: String, directory: String) {
    include(name)
    project(":$name").apply { projectDir = file("$directory/$name") }
}

rootProject.name = "distributor"

include(":distributor-bom")
include(":distributor-core")

includeSub("distributor-js", "distributor-script")
