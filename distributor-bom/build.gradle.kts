plugins {
    `java-platform`
    id("distributor.publishing-conventions")
}

indra {
    configurePublications {
        from(components["javaPlatform"])
    }
}

dependencies {
    constraints {
        for (subproject in rootProject.subprojects) {
            if (subproject == project) {
                continue
            }

            api(project(subproject.path))
        }
    }
}
