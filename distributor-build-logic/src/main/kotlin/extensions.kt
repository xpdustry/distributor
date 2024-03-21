import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.the

internal val Project.libs: LibrariesForLibs get() = the()

open class DistributorModuleExtension(project: Project) {
    val identifier = project.objects.property<String>()
    val display = project.objects.property<String>()
    val main = project.objects.property<String>()
    val description = project.objects.property<String>()

    companion object {
        const val EXTENSION_NAME = "distributorModule"
    }
}
