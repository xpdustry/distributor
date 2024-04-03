plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
}

distributorModule {
    identifier = "distributor-logging"
    display = "DistributorLoggerSimple"
    main = "com.xpdustry.distributor.logging.simple.DistributorLoggerPlugin"
    description = "Simple slf4j logger implementation redirecting to arc.util.Log."
}

dependencies {
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.from.jul)
}
