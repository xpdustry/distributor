import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.internal.catalog.DelegatingProjectDependency
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.the

internal val Project.libs: LibrariesForLibs get() = the()

open class DistributorModuleExtension(project: Project) {
    val identifier = project.objects.property<String>()
    val display = project.objects.property<String>()
    val main = project.objects.property<String>()
    val description = project.objects.property<String>()
    val dependencies = project.objects.listProperty<String>()

    companion object {
        const val EXTENSION_NAME = "distributorModule"
    }
}

val DelegatingProjectDependency.shadowJar
    get() = dependencyProject.provider { dependencyProject.tasks.named<ShadowJar>(ShadowJavaPlugin.SHADOW_JAR_TASK_NAME) }
