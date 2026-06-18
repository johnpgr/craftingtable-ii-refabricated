import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories
import org.gradle.language.jvm.tasks.ProcessResources

fun Project.configureFabricLoader(
    loader: String,
    fabricModJsonProperties: Map<String, Any>,
) {
    val minecraftDependency = mod.dep("minecraft.fabric")
    val javaVersion = mod.prop("java_version")
    val projectName = name

    pluginManager.apply("fabric-loom")
    val loom = extensions.getByType(LoomGradleExtensionAPI::class.java)

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

    dependencies.apply {
        add("minecraft", "com.mojang:minecraft:$minecraftDependency")
        add(
            "mappings",
            loom.layered {
                officialMojangMappings()
                parchment(
                    "org.parchmentmc.data:parchment-${mod.dep("parchment_minecraft")}:${mod.dep("parchment_version")}@zip",
                )
            },
        )
        add("modImplementation", "net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_api_version")}")
    }

    val requiredJava = JavaVersion.toVersion(javaVersion)
    extensions.configure<JavaPluginExtension>("java") {
        targetCompatibility = requiredJava
        sourceCompatibility = requiredJava
    }

    tasks.named<RemapJarTask>("remapJar") {
        inputs.file(tasks.named<Jar>("jar").get().archiveFile)
        archiveClassifier.set(null)
        dependsOn(tasks.named("jar"))
    }

    tasks.named<Jar>("jar") {
        archiveClassifier.set("dev")
    }

    val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
        group = "build"
        from(tasks.named<RemapJarTask>("remapJar").get().archiveFile)
        into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
        dependsOn("build")
    }

    loom.runs {
        configureEach {
            ideConfigGenerated(true)
        }
        named("client") {
            setRunDir(
                projectDir.toPath()
                    .relativize(rootProject.file("run/$projectName/client").toPath())
                    .toString(),
            )
        }
        named("server") {
            setRunDir(
                projectDir.toPath()
                    .relativize(rootProject.file("run/$projectName/server").toPath())
                    .toString(),
            )
        }
    }

    tasks.named<ProcessResources>("processResources") {
        for ((propName, value) in fabricModJsonProperties) {
            inputs.property(propName, value)
        }
        filesMatching("fabric.mod.json.in") {
            expand(fabricModJsonProperties)
            path = "fabric.mod.json"
        }
        compactGeneratedJson()
    }

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${mod.name}" }
        }
    }

    tasks.named("build") {
        group = "build"
        description = "Builds the Fabric artifact."
    }
}
