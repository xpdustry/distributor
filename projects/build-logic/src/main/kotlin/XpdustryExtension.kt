import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

const val GITHUB_REPOSITORY = "xpdustry/monorepo"

abstract class XpdustryExtension {
    /** The plugin settings, read by `xpdustry.mindustry`. */
    @get:Nested abstract val plugin: PluginSettings

    fun plugin(action: Action<PluginSettings>) = action.execute(plugin)
}

abstract class PluginSettings {
    /** The display name, the name of the project in PascalCase by default. */
    abstract val displayName: Property<String>

    /** The main class, extending `mindustry.mod.Plugin`. */
    abstract val mainClass: Property<String>
}
