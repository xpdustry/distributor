enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "distributor-parent"

includeBuild("distributor-build-logic")
include(":distributor-logging-simple")
include(":distributor-common-api")
include(":distributor-common")
include(":distributor-command-cloud")
include(":distributor-permission-rank")
include(":distributor-internal-annotations")
include(":distributor-translation-fluent")
