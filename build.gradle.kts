import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import org.ajoberstar.grgit.Grgit
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

buildscript {
    dependencies {
        classpath("org.kohsuke:github-api:${project.property("github_api_version") as String}")
    }
}

plugins {
    id("maven-publish")
    id("fabric-loom")
    id("org.jetbrains.kotlin.jvm")
    id("org.ajoberstar.grgit")
    id("com.modrinth.minotaur")
    id("com.matthewprenger.cursegradle")
}

operator fun Project.get(property: String): String {
    return property(property) as String
}

version = project["mod_version"]
group = project["maven_group"]

fun getChangeLog(): String {
    return File("changelogs/${version}.md").readText()
}

fun getBranch(): String {
    environment["GITHUB_REF"]?.let { branch ->
        return branch.substring(branch.lastIndexOf("/") + 1)
    }
    val grgit = try {
        extensions.getByName("grgit") as Grgit
    } catch (ignored: Exception) {
        return "unknown"
    }
    val branch = grgit.branch.current().name
    return branch.substring(branch.lastIndexOf("/") + 1)
}

val environment: Map<String, String> = System.getenv()
val releaseName = "${
    name.split("-").joinToString(" ") {
        if (it.length == 2) it.uppercase()
        else it.replaceFirstChar { char -> char.uppercase() }
    }
} ${(version as String).split("+")[0]}"
val releaseType = "RELEASE"
val releaseFile = "${layout.buildDirectory.get()}/libs/${base.archivesName.get()}-${version}.jar"
val cfGameVersion = project["minecraft_version"]

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

base {
    archivesName = project["archives_base_name"]
}

repositories {
    // Add repositories to retrieve artifacts from in here.
}

dependencies {
    minecraft("com.mojang:minecraft:${project["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project["fabric_version"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project["fabric_kotlin_version"]}")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

java {
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

tasks.register("github") {
    dependsOn(tasks.remapJar)
    group = "upload"

    onlyIf { environment.containsKey("GITHUB_TOKEN") }

    doLast {
        val github = GitHub.connectUsingOAuth(environment["GITHUB_TOKEN"])
        val repository = github.getRepository(environment["GITHUB_REPOSITORY"])

        val releaseBuilder = GHReleaseBuilder(repository, version as String)
        releaseBuilder.name(releaseName)
        releaseBuilder.body(getChangeLog())
        releaseBuilder.commitish(getBranch())

        val ghRelease = releaseBuilder.create()
        ghRelease.uploadAsset(file(releaseFile), "application/java-archive")
    }
}

modrinth {
    environment["MODRINTH_TOKEN"]?.let { token.set(it) }
    projectId.set(project["modrinth_id"])
    changelog.set(getChangeLog())
    versionNumber.set(version as String)
    versionName.set(releaseName)
    versionType.set(releaseType.lowercase())
    uploadFile.set(tasks.remapJar.get())
    gameVersions.add(project["minecraft_version"])
    loaders.add("fabric")

    dependencies {
        required.project("fabric-api")
        required.project("fabric-language-kotlin")
    }
}
tasks.modrinth.configure {
    group = "upload"
}

curseforge {
    environment["CURSEFORGE_API_KEY"]?.let { apiKey = it }

    project(closureOf<CurseProject> {
        id = project["curseforge_id"]
        changelog = getChangeLog()
        releaseType = this@Build_gradle.releaseType.lowercase()
        addGameVersion(cfGameVersion)
        addGameVersion("Fabric")

        mainArtifact(file(releaseFile), closureOf<CurseArtifact> {
            displayName = releaseName
            relations(closureOf<CurseRelation> {
                requiredDependency("fabric-api")
                requiredDependency("fabric-language-kotlin")
            })
        })

        afterEvaluate {
            uploadTask.dependsOn("remapJar")
        }
    })

    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}