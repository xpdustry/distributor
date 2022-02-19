import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URL
import fr.xpdustry.toxopid.task.MindustryExec
import java.io.BufferedReader
import java.io.IOException

plugins {
    id("distributor.base-conventions")
    id("distributor.publishing-conventions")
}

dependencies {
    compileOnly(project(":distributor-core"))
}

tasks.withType<MindustryExec> {
    addArtifact(project(":distributor-core").tasks.named<Jar>("shadowJar"))
}

tasks.named<ShadowJar>("shadowJar") {
    val regex = Regex("^importPackage\\(Packages\\..+|^const .+ = Packages\\..+")
    val file = temporaryDir.resolve("init.js")

    file.printWriter().use { printer ->
        file("$projectDir/base.js").reader().use { it.copyTo(printer) }
        printer.println()

        val reader = try {
            val mindustryVersion = readJson(file("$rootDir/global-plugin.json"))["minGameVersion"] as String
            URL("https://raw.githubusercontent.com/Anuken/Mindustry/$mindustryVersion/core/assets/scripts/global.js").openStream().bufferedReader()
        } catch (e: IOException) {
            logger.warn("WARNING, unable to read the remote global.js, the import file will be empty...")
            BufferedReader.nullReader()
        }

        reader.useLines { it.forEach { s -> if (regex.matches(s)) printer.println(s) } }
    }

    from(file)
}