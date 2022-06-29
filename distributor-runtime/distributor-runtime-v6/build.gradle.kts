plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
}

dependencies {
    compileOnly(project(":distributor-core"))
}