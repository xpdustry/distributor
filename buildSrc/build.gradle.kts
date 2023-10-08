plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori:indra-common:3.1.2")
    implementation("net.kyori:indra-licenser-spotless:3.1.2")
    implementation("fr.xpdustry:toxopid:3.2.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:2.0.2")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.11.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.8.20")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.46.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
