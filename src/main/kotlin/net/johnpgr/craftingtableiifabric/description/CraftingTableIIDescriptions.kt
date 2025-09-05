package net.johnpgr.craftingtableiifabric.description

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.util.*
import net.minecraft.client.MinecraftClient
import java.io.File
import kotlin.jvm.optionals.getOrNull

@Environment(EnvType.CLIENT)
object CraftingTableIIDescriptions {
    private const val FALLBACK_LANG = "en_us"
    val descriptions: HashMap<String, String> by lazy { loadDescriptions() }

    fun descriptionFile(lang: String): File =
        File("${FabricLoader.getInstance().configDir}${File.separator}${CraftingTableIIMod.MOD_ID}${File.separator}descriptions${File.separator}${lang}.json")

    fun register() {
        ClientLifecycleEvents.CLIENT_STARTED.register { descriptions }
    }

    private fun loadDescriptions(): HashMap<String, String> {
        CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] Trying to read descriptions file...")
        val client = MinecraftClient.getInstance()
        val resourceManager = client.resourceManager
        var currentLang = client.languageManager.language

        var descriptionResource = resourceManager.getResource(
            CraftingTableIIMod.id("descriptions/${currentLang}.json")
        ).getOrNull()

        if (descriptionResource == null) {
            currentLang = FALLBACK_LANG
            descriptionResource = resourceManager.getResource(
                CraftingTableIIMod.id("descriptions/$FALLBACK_LANG.json")
            ).getOrNull()
        }

        if (descriptionResource == null) {
            CraftingTableIIMod.LOGGER.error("[${CraftingTableIIMod.MOD_ID}] Failed to load descriptions")
            return hashMapOf()
        }

        val defaultDescriptions = descriptionResource.loadJsonToMap()

        return try {
            val descriptionsFile = descriptionFile(currentLang)
            descriptionsFile.parentFile.mkdirs()

            if (descriptionsFile.createNewFile()) {
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] No descriptions file found, creating a new one...")
                defaultDescriptions.writeToJsonFile(descriptionsFile)
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] Successfully created default descriptions file.")
                defaultDescriptions
            } else {
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] A descriptions file was found, loading it..")
                val loadedDescriptions = descriptionsFile.readJsonAsMap()
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] Successfully loaded descriptions file.")
                loadedDescriptions
            }
        } catch (ex: Exception) {
            CraftingTableIIMod.LOGGER.error(
                "[${CraftingTableIIMod.MOD_ID}] There was an error creating/loading the descriptions file!",
                ex
            )
            defaultDescriptions
        }
    }
}

