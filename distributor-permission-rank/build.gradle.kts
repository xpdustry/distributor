plugins {
    id("distributor4.base-conventions")
    id("distributor4.mindustry-conventions")
}

module {
    identifier = "distributor-permission"
    display = "DistributorLoggerSimple"
    main = "com.xpdustry.distributor.logger.simple.DistributorLoggerPlugin"
    description = "Simple permission system based on linear ranks."
}

dependencies {
    compileOnly(project(":distributor-common"))
}
