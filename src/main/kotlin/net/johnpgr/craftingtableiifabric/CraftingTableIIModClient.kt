package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ClientModInitializer
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityModel
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer
import net.johnpgr.craftingtableiifabric.renderer.CraftingTableIIItemDynamicRenderer
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreen

object CraftingTableIIModClient : ClientModInitializer {
    override fun onInitializeClient() {
        CraftingTableIIDescriptions.register()
        CraftingTableIIScreen.register()
        CraftingTableIIEntityModel.register()
        CraftingTableIIEntityRenderer.register()
        // Note: Dynamic item rendering registration is handled differently in 1.21.4+
        // CraftingTableIIItemDynamicRenderer.register()
    }
}
