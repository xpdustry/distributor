plugins {
    id("distributor.base-conventions")
    id("distributor.publish-conventions")
}

dependencies {
    compileOnly(projects.distributorInternalAnnotations)
    annotationProcessor(libs.immutables.processor)
    compileOnlyApi(libs.slf4j.api)
    compileOnlyApi(libs.bundles.mindustry)
    testImplementation(libs.bundles.mindustry)
}
