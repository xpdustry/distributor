import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File
import java.security.MessageDigest
import java.util.HexFormat
import java.util.zip.ZipFile
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateDistributionManifest : DefaultTask() {

    /** The plugin jars, their name and dependencies are read from their `plugin.json`. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.NONE) abstract val plugins: ConfigurableFileCollection

    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val mindustryServer: RegularFileProperty

    @get:Input abstract val mindustryVersion: Property<String>

    @get:Input abstract val mindustryUrl: Property<String>

    @get:Input abstract val distributionVersion: Property<String>

    @get:Input abstract val commit: Property<String>

    @get:Input abstract val committed: Property<String>

    @get:Input abstract val java: Property<Int>

    @get:OutputFile abstract val output: RegularFileProperty

    @TaskAction
    fun generate() {
        val plugins = plugins.files.map(::describe).sortedBy { it["name"] as String }
        val duplicates = plugins.groupBy { it["name"] }.filterValues { it.size > 1 }.keys
        if (duplicates.isNotEmpty()) throw GradleException("Duplicate plugins $duplicates")
        val manifest =
            linkedMapOf(
                "schema" to 1,
                "version" to distributionVersion.get(),
                "commit" to commit.get(),
                "committed" to committed.get(),
                "java" to java.get(),
                "mindustry" to
                    linkedMapOf(
                        "version" to mindustryVersion.get(),
                        "url" to mindustryUrl.get(),
                        "sha256" to sha256(mindustryServer.get().asFile),
                    ),
                "plugins" to plugins,
            )
        output.get().asFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(manifest)) + "\n")
    }

    private fun describe(jar: File): Map<String, Any?> {
        val metadata =
            ZipFile(jar).use { zip ->
                val entry = zip.getEntry("plugin.json") ?: throw GradleException("Missing plugin.json in $jar")
                zip.getInputStream(entry).use { JsonSlurper().parse(it) as Map<*, *> }
            }
        return linkedMapOf(
            "name" to metadata["name"],
            "sha256" to sha256(jar),
            // Only the hard ones, the soft ones never keep a plugin from loading
            "dependencies" to metadata["dependencies"],
        )
    }

    private fun sha256(file: File): String =
        HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(file.readBytes()))
}
