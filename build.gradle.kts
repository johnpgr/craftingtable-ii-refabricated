import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.jvm.tasks.Jar
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.kohsuke:github-api:${findProperty("github_api_version")}")
    }
}

plugins {
    id("org.ajoberstar.grgit")
    id("me.modmuss50.mod-publish-plugin") version "2.0.0"
}

val environment: Map<String, String> = System.getenv()
val releaseName = "${findProperty("mod_name")} ${version.toString().split('+')[0]}"

fun changeLog(): String = file("changelogs/${version}.md").readText()

fun releaseBranch(): String {
    environment["GITHUB_REF"]?.let { ref ->
        return ref.substring(ref.lastIndexOf('/') + 1)
    }
    return try {
        grgit.branch.current().name.substringAfterLast('/')
    } catch (_: Exception) {
        "unknown"
    }
}

tasks.register("build") {
    group = "build"
    description = "Builds all loaders."
    dependsOn(":fabric:buildAndCollect", ":neoforge:buildAndCollect")
}

gradle.projectsEvaluated {
    val fabricReleaseJar = project(":fabric").tasks.named("remapJar")
    val neoforgeReleaseJar = project(":neoforge").tasks.named("jar")

    publishMods {
        changelog.set(provider { changeLog() })

        modrinth("modrinth-fabric") {
            projectId.set(findProperty("modrinth_id") as String)
            accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
            file.set(fabricReleaseJar.flatMap { (it as AbstractArchiveTask).archiveFile })
            displayName.set(releaseName)
            version.set(project.version.toString())
            type.set(STABLE)
            modLoaders.add("fabric")
            minecraftVersions.add(findProperty("minecraft_version") as String)
            requires("fabric-api")
        }

        modrinth("modrinth-neoforge") {
            projectId.set(findProperty("modrinth_id") as String)
            accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
            file.set(neoforgeReleaseJar.flatMap { (it as Jar).archiveFile })
            displayName.set("$releaseName (NeoForge)")
            version.set(project.version.toString())
            type.set(STABLE)
            modLoaders.add("neoforge")
            minecraftVersions.add(findProperty("minecraft_version") as String)
        }

        curseforge("curseforge-fabric") {
            projectId.set(findProperty("curseforge_id") as String)
            accessToken.set(providers.environmentVariable("CURSEFORGE_API_KEY"))
            file.set(fabricReleaseJar.flatMap { (it as AbstractArchiveTask).archiveFile })
            displayName.set(releaseName)
            version.set(project.version.toString())
            type.set(STABLE)
            modLoaders.add("fabric")
            minecraftVersions.add(findProperty("minecraft_version") as String)
            client.set(true)
            server.set(true)
            requires("fabric-api")
        }

        curseforge("curseforge-neoforge") {
            projectId.set(findProperty("curseforge_id") as String)
            accessToken.set(providers.environmentVariable("CURSEFORGE_API_KEY"))
            file.set(neoforgeReleaseJar.flatMap { (it as Jar).archiveFile })
            displayName.set("$releaseName (NeoForge)")
            version.set(project.version.toString())
            type.set(STABLE)
            modLoaders.add("neoforge")
            minecraftVersions.add(findProperty("minecraft_version") as String)
            client.set(true)
            server.set(true)
        }
    }

    tasks.named("publishMods").configure {
        dependsOn(fabricReleaseJar, neoforgeReleaseJar)
        group = "upload"
    }

    tasks.register("github") {
        dependsOn(fabricReleaseJar, neoforgeReleaseJar)
        group = "upload"
        onlyIf { environment.containsKey("GITHUB_TOKEN") }

        doLast {
            val github = GitHub.connectUsingOAuth(environment["GITHUB_TOKEN"])
            val repository = github.getRepository(environment["GITHUB_REPOSITORY"])
            val releaseBuilder = GHReleaseBuilder(repository, version.toString())
            releaseBuilder.name(releaseName)
            releaseBuilder.body(changeLog())
            releaseBuilder.commitish(releaseBranch())
            val ghRelease = releaseBuilder.create()

            listOf(fabricReleaseJar, neoforgeReleaseJar).forEach { jarTask ->
                val jarFile = (jarTask.get() as AbstractArchiveTask).archiveFile.get().asFile
                ghRelease.uploadAsset(jarFile, "application/java-archive")
            }
        }
    }
}
