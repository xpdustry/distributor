plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
}

module {
    identifier = "distributor-logger"
    display = "DistributorLoggerSimple"
    main = "com.xpdustry.distributor.logger.simple.DistributorLoggerPlugin"
    description = "Simple slf4j logger implementation for plugins."
}

dependencies {
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.from.jul)
}
