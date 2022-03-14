import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import fr.xpdustry.toxopid.task.MindustryExec

plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
}

dependencies {
    compileOnly(project(":distributor-core"))
    implementation("org.mozilla:rhino:1.7.14")
}

tasks.withType<MindustryExec> {
    addArtifact(project(":distributor-core").tasks.named<Jar>("shadowJar"))
}

tasks.named<ShadowJar>("shadowJar") {
    from("$projectDir/init.js")
}
