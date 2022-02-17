pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("build-logic")
}

rootProject.name = "distributor"

include(":distributor-bom")
include(":distributor-core")
include("distributor-js")

project(":distributor-js").apply {
    projectDir = file("distributor-script/distributor-js")
}
