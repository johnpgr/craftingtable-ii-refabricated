import net.neoforged.moddevgradle.dsl.NeoForgeExtension
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

fun Project.configureNeoForgeLoader(loader: String) {
    val javaVersion = mod.prop("java_version")
    val projectName = name

    pluginManager.apply("net.neoforged.moddev")

    extensions.configure<BasePluginExtension>("base") {
        archivesName.set("${mod.id}-$loader")
    }

    extensions.configure<SourceSetContainer>("sourceSets") {
        named("main") {
            java.srcDir(rootProject.file("common/src/main/java"))
            resources.srcDir(rootProject.file("common/src/main/resources"))
        }
    }

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
        group = "build"
        description = "Builds the NeoForge artifact."
    }
}
