plugins {
    id("net.kyori.indra.git")
}

tasks.create("createRelease") {
    dependsOn("requireClean")

    doLast {
        exec {
            commandLine("git", "tag", "-as", "v${project.version}", "-F", "./CHANGELOG.md")
        }

        exec {
            commandLine("git", "push", "origin", "--tags")
        }
    }
}
