plugins {
    alias(conventions.plugins.xpdustry.common)
    alias(conventions.plugins.xpdustry.maven)
}

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
        }
    }
}
