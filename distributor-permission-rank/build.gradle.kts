plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publish-conventions")
}

distributorModule {
    identifier = "distributor-permission-rank"
    display = "DistributorPermissionRank"
    main = "com.xpdustry.distributor.api.permission.rank.DistributorPermissionRankPlugin"
    description = "Simple permission system based on ranks."
    dependencies = listOf("distributor-common")
}

dependencies {
    compileOnlyApi(projects.distributorCommonApi)
    implementation(libs.configurate.core)
    implementation(libs.configurate.yaml)
    testImplementation(projects.distributorCommon)
    testImplementation(libs.slf4j.api)
    testImplementation(libs.slf4j.simple)
}

tasks.shadowJar {
    val relocationPackage = "com.xpdustry.distributor.api.permission.rank.shadow"
    relocate("org.yaml.snakeyaml", "$relocationPackage.snakeyaml")
    relocate("org.spongepowered.configurate", "$relocationPackage.configurate")
    minimize()
}

tasks.runMindustryServer {
    mods.from(projects.distributorCommon.shadowJar)
}
