plugins {
    id("fabric-loom")
    id("java")
}

version = "${mod.version}+${mod.prop("mc_title")}"
group = mod.group

val fabricModJsonProperties = mapOf(
    "mod_id" to mod.id,
    "mod_name" to mod.name,
    "version" to mod.version,
    "minecraft_targets" to mod.prop("mc_targets_fabric"),
    "java_version" to mod.prop("java_version"),
    "fabric_loader_version" to mod.dep("fabric_loader"),
    "mod_author" to requireNotNull(rootProject.prop("mod_author")) { "Missing 'mod_author' in gradle.properties" },
    "license" to requireNotNull(rootProject.prop("license")) { "Missing 'license' in gradle.properties" },
    "description" to requireNotNull(rootProject.prop("description")) { "Missing 'description' in gradle.properties" },
)

prepareExpandedResource(
    rootProject.file("fabric/src/main/resources/fabric.mod.json.in"),
    "fabric.mod.json",
    fabricModJsonProperties,
)

configureFabricLoader(
    loader = "fabric",
    fabricModJsonProperties = fabricModJsonProperties,
)
