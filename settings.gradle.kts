pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("build-logic")
}

rootProject.name = "distributor-parent"

include(":distributor-bom")
include(":distributor-core")
