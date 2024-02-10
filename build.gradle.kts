plugins {
    id("distributor4.parent-conventions")
}

version = "4.0.0" + if (indraGit.headTag() == null) "-SNAPSHOT" else ""
group = "com.xpdustry"
description = "The Mindustry plugin of ur dreams..."

tasks {
    spotlessCheck {
        dependsOn(gradle.includedBuild("distributor-build-logic").task(":spotlessCheck"))
    }
    spotlessApply {
        dependsOn(gradle.includedBuild("distributor-build-logic").task(":spotlessApply"))
    }
}
