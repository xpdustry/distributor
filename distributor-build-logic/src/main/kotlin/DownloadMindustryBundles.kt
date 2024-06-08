import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Files
import java.util.Properties
import java.util.zip.ZipInputStream
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter
import kotlin.io.path.moveTo

@CacheableTask
open class DownloadMindustryBundles : DefaultTask() {
    @get:Input
    val version = project.objects.property<String>()

    init {
        outputs.files(project.fileTree(temporaryDir))
    }

    @TaskAction
    fun download() {
        temporaryDir.deleteRecursively()
        temporaryDir.mkdirs()

        val uri = URI.create("https://github.com/Anuken/Mindustry/archive/refs/tags/${version.get()}.zip")

        val response =
            HTTP.send(HttpRequest.newBuilder(uri).GET().build(), BodyHandlers.ofInputStream())

        if (response.statusCode() != 200) {
            throw RuntimeException("Failed to download $uri")
        }

        val directory = temporaryDir.toPath()
        ZipInputStream(response.body()).use { zip ->
            var entry = zip.getNextEntry()
            while (entry != null) {
                if (entry.name.contains("/core/assets/bundles/") && !entry.isDirectory) {
                    val file = directory.resolve(entry.name.split("/").last())
                    Files.newOutputStream(file).use { output ->
                        // https://stackoverflow.com/a/22646404
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (zip.read(buffer).also { bytesRead = it } != -1 &&
                            bytesRead <= entry!!.size
                        ) {
                            output.write(buffer, 0, bytesRead)
                        }
                    }
                }
                zip.closeEntry()
                entry = zip.getNextEntry()
            }
        }

        Files.list(directory).forEach { file ->
            val properties = Properties()
            file.bufferedReader().use(properties::load)
            val result = Properties()
            properties.keys.forEach {
                if ((it as String).matches(CONTENT_NAME_REGEX)) {
                    result["mindustry.$it"] = properties[it]
                }
            }
            file.bufferedWriter().use { result.store(it, null) }
        }

        directory.resolve("bundle.properties").moveTo(directory.resolve("bundle_en.properties"))
    }

    companion object {
        private val CONTENT_NAME_REGEX = Regex("^(block|unit|item|liquid|weather|status|planet|team)\\.(.+)\\.name$")
        private val HTTP =
            HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
    }
}
