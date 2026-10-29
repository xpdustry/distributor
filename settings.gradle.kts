@file:Suppress("UnstableApiUsage")

import java.time.Instant
import java.time.ZoneOffset

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        maven("https://maven.xpdustry.com/mindustry") {
            name = "anuke-xpdustry"
            mavenContent { includeGroupByRegex("^com\\.github\\.Anuken(\\.[\\w-]+)*$") }
            metadataSources {
                gradleMetadata()
                mavenPom()
                artifact()
            }
        }
        exclusiveContent {
            forRepository {
                ivy("https://github.com") {
                    name = "mindustry-releases"
                    patternLayout { artifact("[organisation]/Mindustry/releases/download/[revision]/[module].[ext]") }
                    metadataSources { artifact() }
                }
            }
            filter {
                includeGroup("Anuken")
            }
        }
    }
}

rootProject.name = "xpdustry-monorepo"

includeBuild("projects/build-logic")

fun includeProject(id: String) {
    include(id)
    project(":$id").projectDir = file("projects/$id")
}

includeProject("foundation")
includeProject("nucleus")
includeProject("sql-postgresql")
includeProject("sql-postgresql-embedded")

// The action is isolated, so it must only capture the string
calendarVersion().let { calendar ->
    gradle.lifecycle.beforeProject {
        group = "com.xpdustry"
        version = calendar
    }
}

fun calendarVersion(): String {
    if (!file(".git").exists()) return "0.0.0-SNAPSHOT"
    val committed = providers.exec {
        commandLine("git", "show", "--no-patch", "--format=%ct", "HEAD")
        // Fails without any commit
        isIgnoreExitValue = true
    }
    if (committed.result.get().exitValue != 0) return "0.0.0-SNAPSHOT"
    val date = Instant.ofEpochSecond(committed.standardOutput.asText.get().trim().toLong()).atZone(ZoneOffset.UTC)
    val distributorReleaseTags = "v[0-9].*"
    val describe = providers.exec {
        commandLine("git", "describe", "--tags", "--match", "v*", "--exclude", distributorReleaseTags, "--long")
        // Fails without any tag
        isIgnoreExitValue = true
    }
    val next = if (describe.result.get().exitValue != 0) {
        "${date.year}.${date.monthValue}.0"
    } else {
        val nearest = describe.standardOutput.asText.get().trim()
        val match = Regex("""v(\d+)\.(\d+)\.(\d+)-(\d+)-g\p{XDigit}+""").matchEntire(nearest)
        requireNotNull(match) { "The nearest tag of $nearest is not a vYEAR.MONTH.BUILD release" }
        val (year, month, build, distance) = match.destructured.toList().map(String::toInt)
        if (distance == 0) return "$year.$month.$build"
        val sameOrEarlierMonth = date.year * 12 + date.monthValue <= year * 12 + month
        if (sameOrEarlierMonth) "$year.$month.${build + 1}" else "${date.year}.${date.monthValue}.0"
    }
    val isRelease = providers.gradleProperty("release").map(String::toBoolean).getOrElse(false)
    return if (isRelease) next else "$next-SNAPSHOT"
}
