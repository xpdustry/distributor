plugins {
    id("distributor4.base-conventions")
    id("distributor4.mindustry-conventions")
}

module {
    identifier = "distributor-permission-rank"
    display = "DistributorPermissionRank"
    main = "com.xpdustry.distributor.permission.rank.DistributorPermissionRankPlugin"
    description = "Simple permission system based on ranks."
}

dependencies {
    compileOnly(project(":distributor-common"))
}
