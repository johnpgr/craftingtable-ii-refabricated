plugins {
    id("net.neoforged.moddev")
    id("java")
}

version = "${mod.version}+${mod.prop("mc_title")}"
group = mod.group

configureNeoForgeLoader(loader = "neoforge")
