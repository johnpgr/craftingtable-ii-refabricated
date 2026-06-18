import com.google.gson.Gson
import com.google.gson.JsonParser
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

fun compactJson(text: String): String = Gson().toJson(JsonParser.parseString(text))

fun ProcessResources.compactGeneratedJson() {
    doLast {
        val outputDir = outputs.files.singleFile
        project.fileTree(outputDir).matching {
            include("**/*.json")
        }.forEach { jsonFile: File ->
            jsonFile.writeText(compactJson(jsonFile.readText()))
        }
    }
}

val Project.mod: ModData get() = ModData(this)

fun Project.prop(key: String): String? = findProperty(key)?.toString()

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) {
        inputs.property(name, value)
    }
    filesMatching(files) {
        expand(properties.toMap())
    }
}

/** Expands a resource template for Loom, which requires valid JSON at configuration time. */
fun Project.prepareExpandedResource(template: File, outputName: String, properties: Map<String, Any>): File {
    var text = template.readText()
    for ((key, value) in properties) {
        text = text.replace("\${$key}", value.toString())
    }
    val output = layout.buildDirectory.file("generated/loom/$outputName").get().asFile
    output.parentFile.mkdirs()
    output.writeText(compactJson(text))
    return output
}

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = requireNotNull(project.prop("mod.id")) { "Missing 'mod.id' in gradle.properties" }
    val name: String get() = requireNotNull(project.prop("mod.name")) { "Missing 'mod.name' in gradle.properties" }
    val version: String get() = requireNotNull(project.prop("mod.version")) { "Missing 'mod.version' in gradle.properties" }
    val group: String get() = requireNotNull(project.prop("mod.group")) { "Missing 'mod.group' in gradle.properties" }

    fun prop(key: String) = requireNotNull(project.prop("mod.$key")) { "Missing 'mod.$key' in gradle.properties" }
    fun dep(key: String) = requireNotNull(project.prop("dep.$key")) { "Missing 'dep.$key' in gradle.properties" }
}
