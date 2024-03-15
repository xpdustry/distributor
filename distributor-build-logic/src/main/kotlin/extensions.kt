import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.kyori.mammoth.Extensions
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import org.gradle.kotlin.dsl.the

internal val Project.libs: LibrariesForLibs get() = the()

inline fun <reified T : Any> ExtensionContainer.findOrCreateExtension(name: String): T = Extensions.findOrCreate(this, name, T::class.java)

open class DistributorModuleExtension(project: Project) {
    val identifier = project.objects.property<String>()
    val display = project.objects.property<String>()
    val main = project.objects.property<String>()
    val description = project.objects.property<String>()
    val dependencies = project.objects.setProperty<ProjectDependency>()

    companion object {
        const val EXTENSION_NAME = "distributorModule"
    }
}

fun Project.collectAllPluginDependencies(): Set<TaskProvider<out ShadowJar>> {
    val dependencies = mutableSetOf<TaskProvider<out ShadowJar>>()
    val extension = extensions.findOrCreateExtension<DistributorModuleExtension>(DistributorModuleExtension.EXTENSION_NAME)
    for (dependency in extension.dependencies.get()) {
        dependencies += dependency.dependencyProject.tasks.named<ShadowJar>("shadowJar")
        dependencies += dependency.dependencyProject.collectAllPluginDependencies()
    }
    return dependencies
}
