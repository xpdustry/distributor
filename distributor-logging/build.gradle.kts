plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
    id("distributor.mindustry-conventions")
}

dependencies {
    compileOnly(project(":distributor-core"))
}

tasks.runMindustryServer {
    mods.from(
        project(":distributor-core").tasks.named("shadowJar"),
        tasks.shadowJar
    )
}
