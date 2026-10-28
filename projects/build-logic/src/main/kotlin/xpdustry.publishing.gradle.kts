plugins {
    alias(conventions.plugins.xpdustry.common)
    `maven-publish`
    signing
}

// The version is set in the settings, before the project is configured
val isRelease = !version.toString().endsWith("-SNAPSHOT")

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xdoclint", "-Xdoclint:-missing"))
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:-missing", true)
}

publishing {
    publications.register<MavenPublication>("maven") {
        from(components["java"])
        pom {
            name = project.name
            description = provider { project.description }
            url = "https://github.com/$GITHUB_REPOSITORY"

            licenses {
                license {
                    name = "The MIT License"
                    url = "https://opensource.org/licenses/MIT"
                }
            }

            organization {
                name = "xpdustry"
                url = "https://xpdustry.com"
            }

            developers {
                developer {
                    id = "Phinner"
                    timezone = "Europe/Brussels"
                }
            }

            scm {
                connection = "scm:git:https://github.com/$GITHUB_REPOSITORY.git"
                developerConnection = "scm:git:ssh://git@github.com/$GITHUB_REPOSITORY.git"
                url = "https://github.com/$GITHUB_REPOSITORY"
            }

            issueManagement {
                system = "GitHub"
                url = "https://github.com/$GITHUB_REPOSITORY/issues"
            }

            ciManagement {
                system = "GitHub Actions"
                url = "https://github.com/$GITHUB_REPOSITORY/actions"
            }
        }
    }

    repositories {
        maven {
            name = "xpdustry"
            url = uri("https://maven.xpdustry.com/${if (isRelease) "releases" else "snapshots"}")
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    useInMemoryPgpKeys(findProperty("signingKey")?.toString(), findProperty("signingPassword")?.toString())
    sign(publishing.publications)
}

// Only the releases are signed
tasks.withType<Sign>().configureEach {
    enabled = isRelease
}
