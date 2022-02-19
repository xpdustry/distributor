import groovy.json.JsonSlurper
import java.io.File

typealias KtLazyMap = MutableMap<String, Any>

@Suppress("UNCHECKED_CAST")
fun readJson(file: File): KtLazyMap = JsonSlurper().parse(file) as KtLazyMap
