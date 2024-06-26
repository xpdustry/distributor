plugins {
    id("net.kyori.indra.publishing")
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

indra {
    publishReleasesTo("xpdustry", "https://maven.xpdustry.com/releases")
    publishSnapshotsTo("xpdustry", "https://maven.xpdustry.com/snapshots")

    gpl3OnlyLicense()

    github("xpdustry", "distributor") {
        ci(true)
        issues(true)
        scm(true)
    }

    configurePublications {
        pom {
            organization {
                name = "Xpdustry"
                url = "https://www.xpdustry.com"
            }

            developers {
                developer {
                    id = "phinner"
                    url = "https://github.com/phinner"
                    timezone = "Europe/Brussels"
                }
            }
        }
    }
}
