plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publish-conventions")
}

distributorModule {
    identifier = "distributor-permission-rank"
    display = "DistributorPermissionRank"
    main = "com.xpdustry.distributor.permission.rank.DistributorPermissionRankPlugin"
    description = "Simple permission system based on ranks."
}

dependencies {
    pluginCompileOnlyApi(projects.distributorCommon)
    implementation(libs.configurate.core)
    implementation(libs.configurate.yaml)
}

tasks.shadowJar {
    minimize()
}
