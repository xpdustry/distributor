import net.kyori.mammoth.Extensions
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.the

internal val Project.libs: LibrariesForLibs get() = the()

inline fun <reified T : Any> ExtensionContainer.findOrCreateExtension(name: String): T = Extensions.findOrCreate(this, name, T::class.java)

open class DistributorModuleExtension(project: Project) {
    val identifier: Property<String> = project.objects.property(String::class.java)
    val display: Property<String> = project.objects.property(String::class.java)
    val main: Property<String> = project.objects.property(String::class.java)
    val description: Property<String> = project.objects.property(String::class.java)

    // TODO Does not handle transitive dependencies, FIX IT
    val dependencies: SetProperty<Project> = project.objects.setProperty(Project::class.java)
}
