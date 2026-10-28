@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.panuszewski.typesafe-conventions") version "0.12.1"
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "build-logic"
