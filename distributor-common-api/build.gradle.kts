plugins {
    id("distributor.base-conventions")
    id("distributor.publish-conventions")
}

dependencies {
    compileOnlyApi(libs.immutables.annotations)
    annotationProcessor(libs.immutables.processor)
    compileOnlyApi(libs.slf4j.api)
    compileOnlyApi(libs.bundles.mindustry)
}
