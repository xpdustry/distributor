plugins {
    id("distributor.base-conventions")
}

dependencies {
    api(libs.immutables.annotations)
    runtimeOnly(libs.immutables.annotations)
}
