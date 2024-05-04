plugins {
    id("distributor.base-conventions")
    id("distributor.publish-conventions")
}

dependencies {
    compileOnly(projects.distributorInternalAnnotations)
    testImplementation(projects.distributorCommon)
    annotationProcessor(libs.immutables.processor)
    compileOnlyApi(libs.slf4j.api)
    testImplementation(libs.slf4j.simple)
    compileOnlyApi(libs.bundles.mindustry)
    testImplementation(libs.bundles.mindustry)
}
