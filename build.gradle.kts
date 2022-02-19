plugins {
    id("distributor.parent-build-logic")
}

val metadata = readJson(file("global-plugin.json"))

group = "fr.xpdustry"
version = (metadata["version"] as String) + if (indraGit.headTag() == null) "-SNAPSHOT" else ""
description = "The Mindustry plugin of ur dreams..."
