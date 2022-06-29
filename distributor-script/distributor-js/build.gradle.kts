plugins {
    id("distributor.base-conventions")
    id("distributor.mindustry-conventions")
    id("distributor.publishing-conventions")
}

dependencies {
    compileOnly(project(":distributor-core"))
    api("org.mozilla:rhino:1.7.14")
}
