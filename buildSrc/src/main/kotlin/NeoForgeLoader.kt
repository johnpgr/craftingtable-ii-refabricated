import net.neoforged.moddevgradle.dsl.ModModel
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import net.neoforged.moddevgradle.dsl.Parchment
import net.neoforged.moddevgradle.dsl.RunModel
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories
import org.gradle.language.jvm.tasks.ProcessResources

fun Project.configureNeoForgeLoader(loader: String, isActive: Boolean) {
    val javaVersion = mod.prop("java_version")
    val projectName = name

    pluginManager.apply("net.neoforged.moddev")

    extensions.configure<BasePluginExtension>("base") {
        archivesName.set("${mod.id}-$loader")
    }

    extensions.configure<SourceSetContainer>("sourceSets") {
        named("main") {
            resources.srcDir(rootProject.file("common/src/main/resources"))
            resources.srcDir(rootProject.file("neoforge/src/main/resources"))
        }
    }

    versionedJavaSources(
        rootProject.file("common/src/main/java"),
        rootProject.file("neoforge/src/main/java"),
    )

    repositories {
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.parchmentmc.org/")
    }

    val mainSourceSet: SourceSet = extensions.getByType(SourceSetContainer::class.java).named("main").get()

    val neoForge = extensions.getByType(NeoForgeExtension::class.java)
    neoForge.setVersion(mod.dep("neoforge_loader"))

    neoForge.parchment {
        minecraftVersion.set(mod.dep("parchment_minecraft"))
        mappingsVersion.set(mod.dep("parchment_version"))
    }

    neoForge.mods {
        register(mod.id) {
            sourceSet(mainSourceSet)
        }
    }

    neoForge.runs {
        register("client") {
            gameDirectory.set(rootProject.layout.projectDirectory.dir("run/$projectName/client"))
            client()
        }
        register("server") {
            gameDirectory.set(rootProject.layout.projectDirectory.dir("run/$projectName/server"))
            server()
        }
    }

    val requiredJava = JavaVersion.toVersion(javaVersion)
    extensions.configure<JavaPluginExtension>("java") {
        targetCompatibility = requiredJava
        sourceCompatibility = requiredJava
    }

    val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
        group = "build"
        from(tasks.named<Jar>("jar").get().archiveFile)
        into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
        dependsOn("build")
    }

    if (isActive) {
        rootProject.tasks.register("buildActive") {
            group = "project"
            description = "Builds and collects active subproject artifacts."
            dependsOn(buildAndCollect)
        }

        rootProject.tasks.register("testClient") {
            group = "project"
            description = "Launches the client for testing the active NeoForge version."
            dependsOn(tasks.named("runClient"))
        }

        rootProject.tasks.register("testServer") {
            group = "project"
            description = "Launches the server for testing the active NeoForge version."
            dependsOn(tasks.named("runServer"))
        }
    }

    tasks.named<ProcessResources>("processResources") {
        properties(
            listOf("META-INF/neoforge.mods.toml"),
            "mod_id" to mod.id,
            "mod_name" to mod.name,
            "version" to mod.version,
            "minecraft_version_range" to mod.prop("mc_targets_range"),
            "neoforge_loader_version_range" to mod.dep("neoforge_loader_range"),
            "neoforge_version_range" to mod.dep("neoforge_version_range"),
            "mod_author" to requireNotNull(rootProject.prop("mod_author")) { "Missing 'mod_author' in gradle.properties" },
            "license" to requireNotNull(rootProject.prop("license")) { "Missing 'license' in gradle.properties" },
            "description" to requireNotNull(rootProject.prop("description")) { "Missing 'description' in gradle.properties" },
        )
        compactGeneratedJson()
    }

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${mod.name}" }
        }
    }

    tasks.named("build") {
        group = "versioned"
        description = "Stonecutter target build task."
    }
}
