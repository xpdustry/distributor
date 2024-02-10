plugins {
    id("distributor4.base-conventions")
    id("distributor4.mindustry-conventions")
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
