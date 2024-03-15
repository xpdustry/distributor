enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "distributor-parent"

includeBuild("distributor-build-logic")
include(":distributor-logging-simple")
include(":distributor-core")
include(":distributor-command-cloud")
include(":distributor-permission-rank")
