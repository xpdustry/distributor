pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("build-logic")
}

rootProject.name = "distributor"

include(":distributor-bom")
include(":distributor-core")

include("distributor-runtime:distributor-runtime-v6")
include("distributor-runtime:distributor-runtime-v7")
