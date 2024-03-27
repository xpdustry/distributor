plugins {
    id("distributor.base-conventions")
    id("distributor.publish-conventions")
}

dependencies {
    compileOnly(projects.distributorCommon)
    api(libs.cloud.core)
    compileOnly(libs.bundles.mindustry)
}
