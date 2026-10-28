import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.named

/**
 * Exposes or selects the plugin jars of the projects rather than their classes, through a custom [Category] that the
 * variants of the Java components never match.
 */
fun Configuration.usingMindustryPluginVariant(objects: ObjectFactory) {
    attributes.attribute(Category.CATEGORY_ATTRIBUTE, objects.named("mindustry-plugin"))
}
