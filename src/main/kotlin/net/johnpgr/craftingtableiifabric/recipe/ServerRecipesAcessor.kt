package net.johnpgr.craftingtableiifabric.recipe

import com.google.gson.GsonBuilder
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.item.ItemStack
import net.minecraft.recipe.ServerRecipeManager
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.registry.RegistryWrapper
import net.minecraft.server.MinecraftServer
import net.minecraft.util.collection.DefaultedList
import java.io.FileWriter
import java.io.IOException

/**
 * Accessor to get all valid recipes on the server side.
 * Currently used to generate a JSON file with all item IDs for descriptions.
 * This is not supposed to be used in the mod code at all.
 * Just a temporary dev utility.
 */
object ServerRecipesAcessor {
    private var initialized = false
    private var _allValidRecipes: List<String> = listOf()
    val allValidRecipes: List<String> get() = _allValidRecipes

    fun register() {
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            if (!initialized) {
                _allValidRecipes = loadAllValidRecipes(server)
                initialized = true

                writeRecipesToJsonFile()
            }
        }
    }

    private fun writeRecipesToJsonFile() {
        val itemDescriptions = mutableMapOf<String, String>()

        allValidRecipes.forEach { recipe ->
            itemDescriptions[recipe] = ""
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(itemDescriptions)

        try {
            FileWriter("all_items_descriptions.json").use { writer ->
                writer.write(jsonString)
            }
            println("Successfully wrote all item IDs to all_items_descriptions.json")
        } catch (e: IOException) {
            println("Error writing to file: ${e.message}")
        }

    }

    private fun loadAllValidRecipes(server: MinecraftServer): List<String> {
        val recipeManager: ServerRecipeManager = server.recipeManager
        val registries: RegistryWrapper.WrapperLookup = server.registryManager

        val dummyGrid = DefaultedList.ofSize(9, ItemStack.EMPTY)
        val dummyInput = CraftingRecipeInput.create(3, 3, dummyGrid)

        val recipes = mutableListOf<String>()

        for (recipeEntry in recipeManager.values()) {
            val recipe = recipeEntry.value
            val out: ItemStack? = when (recipe) {
                is ShapelessRecipe -> recipe.craft(dummyInput, registries)
                is ShapedRecipe -> recipe.craft(dummyInput, registries)
                else -> null
            }

            if (out != null && !out.isEmpty) {
                recipes.add(out.item.translationKey)
            }
        }

        return recipes.distinct()
    }
}