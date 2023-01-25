plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori:indra-common:3.0.1")
    implementation("net.kyori:indra-licenser-spotless:3.0.1")
    implementation("fr.xpdustry:toxopid:3.0.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:2.0.2")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.11.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.7.20")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
