plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publish-conventions")
}

distributorModule {
    identifier = "catalina-security-anti-nsfw"
    display = "CatalinaSecurityAntiNSFW"
    main = "com.xpdustry.catalina.antinsfw.CatalinaSecurityAntiNSFWPlugin"
    description = "Nohorny 3."
    dependencies = listOf("distributor-common")
}

dependencies {
    compileOnlyApi(projects.distributorCommonApi)
    testImplementation(projects.distributorCommon)
    testImplementation(libs.slf4j.api)
    testImplementation(libs.slf4j.simple)
}

tasks.runMindustryServer {
    mods.from(project(projects.distributorCommon.path).tasks.shadowJar)
}
