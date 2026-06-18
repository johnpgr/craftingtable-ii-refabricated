plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.fabricmc:fabric-loom:1.10-SNAPSHOT")
    implementation("net.neoforged.moddev:net.neoforged.moddev.gradle.plugin:2.0.141")
}
