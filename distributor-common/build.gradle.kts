plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publish-conventions")
}

distributorModule {
    identifier = "distributor-common"
    display = "DistributorCommon"
    main = "com.xpdustry.distributor.common.DistributorCommonPlugin"
    description = "Core classes of distributor."
    dependencies = listOf("distributor-logging")
}

dependencies {
    compileOnlyApi(libs.immutables.annotations)
    annotationProcessor(libs.immutables.processor)
    compileOnlyApi(libs.slf4j.api)
}

tasks.runMindustryServer {
    mods.from(projects.distributorLoggingSimple.shadowJar)
}