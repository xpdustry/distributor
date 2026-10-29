@file:Suppress("UnstableApiUsage")

plugins {
    alias(conventions.plugins.xpdustry.mindustry)
    alias(conventions.plugins.xpdustry.publishing)
}

description = "The foundation of every xpdustry plugin."

xpdustry {
    plugin {
        mainClass = "com.xpdustry.foundation.FoundationPlugin"
    }
}

dependencies {
    compileOnlyApi(libs.apiguardian)
    compileOnlyApi(libs.errorprone.annotations)
    testImplementation(libs.guava.testlib)
    testImplementation(libs.mockito)
    // Runs the JUnit 3 suites of guava-testlib
    testRuntimeOnly(libs.junit.vintage)
}

// Mockito instruments the classes with a Java agent, which the JDK wants declared on the command line
val mockitoAgent = configurations.dependencyScope("mockitoAgent") {
    dependencies.addLater(libs.mockito)
}
val mockitoAgentClasspath = configurations.resolvable("mockitoAgentClasspath") {
    extendsFrom(mockitoAgent.get())
    isTransitive = false
}

tasks.test {
    val agent = files(mockitoAgentClasspath)
    jvmArgumentProviders.add { listOf("-javaagent:${agent.singleFile.absolutePath}") }
    // The agent extends the boot class path, which class data sharing warns about on every run
    jvmArgs("-Xshare:off")
}
