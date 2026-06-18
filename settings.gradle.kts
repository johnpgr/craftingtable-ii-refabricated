pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
    plugins {
        id("fabric-loom") version "1.10-SNAPSHOT"
        id("org.ajoberstar.grgit") version "5.2.2"
        id("me.modmuss50.mod-publish-plugin") version "2.0.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("dev.kikugie.stonecutter") version "0.9.6"
}

stonecutter {
    kotlinController = true
    shared {
        fun mc(loader: String, vararg versions: String) {
            for (version in versions) {
                val targetDir = file("versions/$version-$loader")
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }

                val sourceProps = file("gradle/targets/$version.properties")
                val targetProps = file("versions/$version-$loader/gradle.properties")
                if (sourceProps.exists()) {
                    sourceProps.copyTo(targetProps, overwrite = true)
                }

                val buildscript = when (loader) {
                    "fabric" -> "build-fabric.gradle.kts"
                    "neoforge" -> "build-neoforge.gradle.kts"
                    else -> error("Unsupported loader: $loader")
                }

                version("$version-$loader", version).buildscript(buildscript)
            }
        }

        mc("fabric", "1.21.1", "1.21.3", "1.21.4", "1.21.5", "1.21.6")
        mc("neoforge", "1.21.1", "1.21.3", "1.21.4", "1.21.5", "1.21.6")
    }
    create(rootProject)
}

rootProject.name = "craftingtable-ii-refabricated"
