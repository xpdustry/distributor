plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://repo.xpdustry.fr/releases") {
        name = "xpdustry-releases"
        mavenContent { releasesOnly() }
    }
}

dependencies {
    implementation("fr.xpdustry:toxopid:1.3.2")
    implementation("net.kyori:indra-common:2.1.1")
    implementation("net.kyori:indra-git:2.1.1")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:2.0.2")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.42.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
