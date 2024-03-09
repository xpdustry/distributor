plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publish-conventions")
}

module {
    identifier = "distributor-permission-rank"
    display = "DistributorPermissionRank"
    main = "com.xpdustry.distributor.permission.rank.DistributorPermissionRankPlugin"
    description = "Simple permission system based on ranks."
    dependencies = setOf(project(":distributor-common"))
}

dependencies {
    compileOnly(project(":distributor-common"))
    implementation(libs.configurate.core)
    implementation(libs.configurate.yaml)
}

tasks.runMindustryServer {
    mods.from(project(":distributor-logging-simple").tasks.shadowJar)
}