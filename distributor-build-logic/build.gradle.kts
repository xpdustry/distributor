plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version libs.versions.spotless
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.toxopid)
    implementation(libs.spotless)
    implementation(libs.shadow)
    implementation(libs.bundles.indra)
    implementation(libs.errorprone.gradle)
    implementation(libs.mammoth)
    implementation(libs.gradle.versions)

    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
}

spotless {
    kotlin {
        targetExclude("src/*/kotlin/**.gradle.kts", "build/generated-sources/**")
        ktlint().setEditorConfigPath(file("../.editorconfig"))
    }

    kotlinGradle {
        target("*.gradle.kts", "src/*/kotlin/**.gradle.kts")
        ktlint().setEditorConfigPath(file("../.editorconfig"))
    }
}
