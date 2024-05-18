plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publish-conventions")
}

distributorModule {
    identifier = "distributor-common"
    display = "DistributorCommon"
    main = "com.xpdustry.distributor.DistributorCommonPlugin"
    description = "Core classes of distributor."
    dependencies = listOf("distributor-logging")
}

dependencies {
    implementation(projects.distributorCommonApi)
    implementation(libs.jansi)
}

tasks.shadowJar {
    val relocationPackage = "com.xpdustry.distributor.vanilla.shadow"
    relocate("org.fusesource.jansi", "$relocationPackage.jansi")
    minimize {
        exclude(dependency(projects.distributorCommonApi))
    }
}

tasks.runMindustryServer {
    mods.from(projects.distributorLoggingSimple.shadowJar)
}