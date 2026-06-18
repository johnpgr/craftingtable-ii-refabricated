plugins {
    id("net.neoforged.moddev") apply false
    id("java")
}

version = "${mod.version}+${mod.prop("mc_title")}"
group = mod.group

if (configureStonecutterLoader()) {
    configureNeoForgeLoader(
        loader = stonecutter.current.project.substringAfterLast('-'),
        isActive = stonecutter.current.isActive,
    )

    stonecutter {
        constants {
            put("fabric", false)
            put("neoforge", true)
        }
    }
}
