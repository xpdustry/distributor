import fr.xpdustry.toxopid.ModPlatform
import fr.xpdustry.toxopid.util.anukenJitpack
import fr.xpdustry.toxopid.util.mindustryDependencies
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

plugins {
    id("net.kyori.indra")
    id("fr.xpdustry.toxopid")
}

toxopid {
    platforms.add(ModPlatform.HEADLESS)
}

repositories {
    mavenCentral()
    anukenJitpack()
}

dependencies {
    compileOnly(project(":distributor-core")) {
        exclude(group = "com.github.Anuken.Arc")
        exclude(group = "com.github.Anuken.Mindustry")
    }
    afterEvaluate {
        mindustryDependencies()
    }
}