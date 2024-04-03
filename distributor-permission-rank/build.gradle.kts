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
    dependencies = listOf("distributor-common")
}

dependencies {
    compileOnlyApi(projects.distributorCommon)
    implementation(libs.configurate.core)
    implementation(libs.configurate.yaml)
}

tasks.shadowJar {
    isEnableRelocation = true
    relocationPrefix = "com.xpdustry.distributor.permission.rank.shadow"
    minimize()
}

tasks.runMindustryServer {
    mods.from(projects.distributorCommon.shadowJar, projects.distributorLoggingSimple.shadowJar)
}
