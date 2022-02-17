import groovy.json.JsonSlurper

plugins {
    id("distributor.parent-build-logic")
}

@Suppress("UNCHECKED_CAST")
val metadata = JsonSlurper().parse(file("global-plugin.json")) as Map<String, Any>

group = "fr.xpdustry"
version = (metadata["version"] as String) + if (indraGit.headTag() == null) "-SNAPSHOT" else ""
description = "The Mindustry plugin of ur dreams..."
