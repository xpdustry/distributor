plugins {
    id("distributor.base-conventions")
    id("distributor.publish-conventions")
}

dependencies {
    compileOnly(projects.distributorInternalAnnotations)
    api(libs.lamp.common)
    compileOnlyApi(projects.distributorCommonApi)

    compileOnlyApi(libs.bundles.mindustry)
    compileOnlyApi(libs.immutables.annotations)
    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.immutables.processor)
}
