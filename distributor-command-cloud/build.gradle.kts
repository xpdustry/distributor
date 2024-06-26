plugins {
    id("distributor.base-conventions")
    id("distributor.publish-conventions")
}

dependencies {
    api(libs.cloud.core)
    compileOnlyApi(projects.distributorCommonApi)
    compileOnlyApi(libs.bundles.mindustry)
}
