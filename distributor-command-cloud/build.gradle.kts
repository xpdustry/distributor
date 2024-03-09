plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
        mavenContent { snapshotsOnly() }
    }
}

module {
    identifier = "distributor-command-cloud"
    display = "DistributorLoggerSimple"
    main = "com.xpdustry.distributor.logger.simple.DistributorLoggerPlugin"
    description = "Simple slf4j logger implementation for plugins."
}

dependencies {
    api(libs.cloud.core)
    compileOnly(project(":distributor-common"))
}
