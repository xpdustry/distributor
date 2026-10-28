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
        // The jars attached to the Mindustry releases, which bundle the game assets
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

// Every project shares the group and version of the monorepo. The action is isolated, so it must only capture the string
calendarVersion().let { calendar ->
    gradle.lifecycle.beforeProject {
        group = "com.xpdustry"
        version = calendar
    }
}

// Derived from git only, never the clock, so a commit always builds the same version.
// A tagged commit is its release, the others are the next release after the nearest tag, dated by the commit.
// The release workflow builds with -Prelease=true, other builds are snapshots of the next release
fun calendarVersion(): String {
    // Without git, like in the source archives downloaded from GitHub, the releases are unknown
    if (!file(".git").exists()) return "0.0.0-SNAPSHOT"
    val committed = providers.exec {
        commandLine("git", "show", "--no-patch", "--format=%ct", "HEAD")
        // Fails without any commit
        isIgnoreExitValue = true
    }
    if (committed.result.get().exitValue != 0) return "0.0.0-SNAPSHOT"
    val date = Instant.ofEpochSecond(committed.standardOutput.asText.get().trim().toLong()).atZone(ZoneOffset.UTC)
    val describe = providers.exec {
        // The tags with a single digit major are the releases of distributor, before it became the monorepo
        commandLine("git", "describe", "--tags", "--match", "v*", "--exclude", "v[0-9].*", "--long")
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
        // The commit date may precede the tag, when a commit made before the end of a month is merged after it
        val sameOrEarlierMonth = date.year * 12 + date.monthValue <= year * 12 + month
        if (sameOrEarlierMonth) "$year.$month.${build + 1}" else "${date.year}.${date.monthValue}.0"
    }
    val isRelease = providers.gradleProperty("release").map(String::toBoolean).getOrElse(false)
    return if (isRelease) next else "$next-SNAPSHOT"
}
