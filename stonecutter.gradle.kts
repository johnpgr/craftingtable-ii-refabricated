import me.modmuss50.mpp.ReleaseType.STABLE
import org.gradle.jvm.tasks.Jar
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

buildscript {
    dependencies {
        classpath("org.kohsuke:github-api:${findProperty("github_api_version")}")
    }
}

plugins {
    id("dev.kikugie.stonecutter")
    id("org.ajoberstar.grgit")
    id("me.modmuss50.mod-publish-plugin") version "2.0.0"
}

stonecutter active "1.21.3-neoforge"

stonecutter parameters {
    swaps["mod_version"] = "\"" + findProperty("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"

    constants["release"] = findProperty("mod.id") != "template"
}

val environment: Map<String, String> = System.getenv()
val releaseName = "${findProperty("mod_name")} ${version.toString().split('+')[0]}"

fun changeLog(): String = file("changelogs/${version}.md").readText()

fun releaseBranch(): String {
    environment["GITHUB_REF"]?.let { ref ->
        return ref.substring(ref.lastIndexOf('/') + 1)
    }
    return try {
        val grgit = extensions.getByName("grgit")
        val branch = grgit.javaClass.getMethod("getBranch").invoke(grgit)
        val name = branch.javaClass.getMethod("getCurrent").invoke(branch)
            .javaClass.getMethod("getName").invoke(
                branch.javaClass.getMethod("getCurrent").invoke(branch)
            ) as String
        name.substring(name.lastIndexOf('/') + 1)
    } catch (_: Exception) {
        "unknown"
    }
}

stonecutter.tasks {
    order("buildAndCollect")
}

tasks.register("build") {
    group = "build"
    description = "Build all Stonecutter targets"
    dependsOn(stonecutter.tasks.named("buildAndCollect"))
}

gradle.projectsEvaluated {
    if (System.getProperty("idea.sync.active") == "true") {
        return@projectsEvaluated
    }

    val fabricProjects = subprojects.filter { it.name.endsWith("-fabric") }
    val neoforgeProjects = subprojects.filter { it.name.endsWith("-neoforge") }

    val fabricReleaseJar = fabricProjects.firstNotNullOfOrNull { fabricProject ->
        fabricProject.tasks.findByName("remapJar")?.let { fabricProject.tasks.named("remapJar") }
    } ?: return@projectsEvaluated
    val neoforgeReleaseJar = neoforgeProjects.firstNotNullOfOrNull { neoforgeProject ->
        neoforgeProject.tasks.findByName("jar")?.let { neoforgeProject.tasks.named("jar") }
    }

    publishMods {
        changelog.set(provider { changeLog() })

        modrinth("modrinth-fabric") {
            projectId.set(findProperty("modrinth_id") as String)
            accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
            file.set(fabricReleaseJar.flatMap { (it as Jar).archiveFile })
            displayName.set(releaseName)
            version.set(project.version.toString())
            type.set(STABLE)
            modLoaders.add("fabric")
            minecraftVersions.add(findProperty("minecraft_version") as String)
            requires("fabric-api")
        }

        neoforgeReleaseJar?.let { neoJar ->
            modrinth("modrinth-neoforge") {
                projectId.set(findProperty("modrinth_id") as String)
                accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
                file.set(neoJar.flatMap { (it as Jar).archiveFile })
                displayName.set("$releaseName (NeoForge)")
                version.set(project.version.toString())
                type.set(STABLE)
                modLoaders.add("neoforge")
                minecraftVersions.add(findProperty("minecraft_version") as String)
            }
        }

        curseforge("curseforge-fabric") {
            projectId.set(findProperty("curseforge_id") as String)
            accessToken.set(providers.environmentVariable("CURSEFORGE_API_KEY"))
            file.set(fabricReleaseJar.flatMap { (it as Jar).archiveFile })
            displayName.set(releaseName)
            version.set(project.version.toString())
            type.set(STABLE)
            modLoaders.add("fabric")
            minecraftVersions.add(findProperty("minecraft_version") as String)
            client.set(true)
            server.set(true)
            requires("fabric-api")
        }

        neoforgeReleaseJar?.let { neoJar ->
            curseforge("curseforge-neoforge") {
                projectId.set(findProperty("curseforge_id") as String)
                accessToken.set(providers.environmentVariable("CURSEFORGE_API_KEY"))
                file.set(neoJar.flatMap { (it as Jar).archiveFile })
                displayName.set("$releaseName (NeoForge)")
                version.set(project.version.toString())
                type.set(STABLE)
                modLoaders.add("neoforge")
                minecraftVersions.add(findProperty("minecraft_version") as String)
                client.set(true)
                server.set(true)
            }
        }
    }

    tasks.named("publishMods").configure {
        dependsOn(fabricReleaseJar)
        neoforgeReleaseJar?.let { dependsOn(it) }
        group = "upload"
    }

    tasks.register("github") {
        dependsOn(fabricReleaseJar)
        neoforgeReleaseJar?.let { dependsOn(it) }
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

            listOfNotNull(fabricReleaseJar, neoforgeReleaseJar).forEach { jarTask ->
                val jarFile = (jarTask.get() as Jar).archiveFile.get().asFile
                ghRelease.uploadAsset(jarFile, "application/java-archive")
            }
        }
    }
}
