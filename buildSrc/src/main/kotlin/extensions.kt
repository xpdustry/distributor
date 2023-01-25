import org.gradle.api.artifacts.dsl.DependencyHandler

val cloud = "1.8.0"
fun cloudCommandFramework(module: String) =
    "cloud.commandframework:cloud-$module:$cloud"
