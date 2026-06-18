pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
    plugins {
        id("fabric-loom") version "1.10-SNAPSHOT"
        id("net.neoforged.moddev") version "2.0.141"
        id("net.neoforged.gradle.userdev") version "7.0.192"
        id("org.ajoberstar.grgit") version "5.2.2"
        id("me.modmuss50.mod-publish-plugin") version "2.0.0"
    }
}

rootProject.name = "craftingtable-ii-refabricated"

include("fabric")
include("neoforge")
