import com.google.gson.Gson
import com.google.gson.JsonParser
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
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

/** During IDE sync, only fully configure the active Stonecutter target (Loom/NeoForge, MC deps, etc.). */
fun Project.configureStonecutterLoader(): Boolean {
    if (System.getProperty("idea.sync.active") != "true") {
        return true
    }
    return isActiveStonecutterTarget()
}

private fun Project.isActiveStonecutterTarget(): Boolean {
    val stonecutter = extensions.findByName("stonecutter") ?: return false
    return try {
        val current = stonecutter.javaClass.getMethod("getCurrent").invoke(stonecutter)
        current.javaClass.getMethod("isActive").invoke(current) as? Boolean ?: false
    } catch (_: Exception) {
        false
    }
}

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

fun Project.versionedJavaSources(vararg roots: File) {
    val generatedSources = layout.buildDirectory.dir("generated/preprocessed/main")
    val version = project.name.substringBeforeLast('-')

    fun generateVersionedSources(outputRoot: File) {
        outputRoot.deleteRecursively()
        outputRoot.mkdirs()

        for (root in roots) {
            if (!root.exists()) {
                continue
            }

            root.walkTopDown()
                .filter { it.isFile && it.extension == "java" }
                .forEach { file ->
                    val relative = root.toPath().relativize(file.toPath())
                    val output = outputRoot.toPath().resolve(relative).toFile()
                    output.parentFile.mkdirs()
                    output.writeText(Preprocessor.transform(file.readLines(), version))
                }
        }
    }

    val prepareSources = tasks.register("prepareVersionedJavaSources") {
        inputs.files(roots)
        outputs.dir(generatedSources)
        dependsOn(tasks.matching { it.name == "stonecutterGenerate" })

        doLast {
            generateVersionedSources(generatedSources.get().asFile)
        }
    }

    // Generate eagerly during IDE sync so diagnostics use a single resolved branch.
    if (System.getProperty("idea.sync.active") == "true") {
        generateVersionedSources(generatedSources.get().asFile)
    }

    extensions.getByType<SourceSetContainer>().named("main") {
        java.setSrcDirs(listOf(generatedSources))
    }

    tasks.named("compileJava") {
        dependsOn(prepareSources)
        dependsOn(tasks.matching { it.name == "stonecutterGenerate" })
    }
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
