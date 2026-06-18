import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("net.neoforged.gradle.userdev")
    id("java")
}

version = "${mod.version}+${mod.prop("mc_title")}"
group = mod.group

base {
    archivesName.set("${mod.id}-neoforge")
}

sourceSets {
    named("main") {
        java.srcDir(rootProject.file("common/src/main/java"))
        resources.srcDir(rootProject.file("common/src/main/resources"))
    }
}

repositories {
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.parchmentmc.org/")
}

dependencies {
    implementation("net.neoforged:forge:${mod.dep("neoforge_loader")}")
}

java {
    val requiredJava = JavaVersion.toVersion(mod.prop("java_version"))
    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
    toolchain.languageVersion.set(JavaLanguageVersion.of(mod.prop("java_version")))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(mod.prop("java_version").toInt())
}

tasks.named<ProcessResources>("processResources") {
    properties(
        listOf("META-INF/mods.toml"),
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

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.named<Jar>("jar").get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/neoforge"))
    dependsOn("build")
}
