plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publish-conventions")
}

distributorModule {
    identifier = "distributor-core"
    display = "DistributorCore"
    main = "com.xpdustry.distributor.core.DistributorCorePlugin"
    description = "Core classes of distributor."
}

dependencies {
    pluginCompileOnlyApi(projects.distributorLoggingSimple)
    compileOnlyApi(libs.immutables.annotations)
    annotationProcessor(libs.immutables.processor)
    compileOnlyApi(libs.slf4j.api)
}
