plugins {
    id("distributor.base-conventions")
    id("distributor.publish-conventions")
}

dependencies {
    testImplementation(projects.distributorCommon)
    compileOnlyApi(libs.slf4j.api)
    testImplementation(libs.slf4j.simple)
    compileOnlyApi(libs.bundles.mindustry)
    testImplementation(libs.bundles.mindustry)
    implementation(libs.geantyref)
    compileOnly(projects.distributorInternalAnnotations)
    annotationProcessor(libs.immutables.processor)
}
