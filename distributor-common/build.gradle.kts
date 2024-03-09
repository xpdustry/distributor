plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
}

module {
    identifier = "distributor-core"
    display = "DistributorCore"
    main = "com.xpdustry.distributor.core.DistributorCorePlugin"
    description = "Core classes of distributor."
    dependencies = setOf(project(":distributor-logging-simple"))
}

dependencies {
    compileOnlyApi(libs.immutables)
    annotationProcessor(libs.immutables)
    compileOnlyApi(libs.slf4j.api)
}
