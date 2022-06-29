pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("build-logic")
}

rootProject.name = "distributor"

include(":distributor-bom")
include(":distributor-core")
