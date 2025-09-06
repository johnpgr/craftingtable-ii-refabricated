package net.johnpgr.craftingtableiifabric

//import net.johnpgr.craftingtableiifabric.renderer.CraftingTableIIItemDynamicRenderer
import net.fabricmc.api.ClientModInitializer
import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntityModel
import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntityRenderer
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions
import net.johnpgr.craftingtableiifabric.renderer.CraftingTableIISpecialRenderer
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreen

object CraftingTableIIModClient : ClientModInitializer {
    override fun onInitializeClient() {
        CraftingTableIIDescriptions.register()
        CraftingTableIIScreen.register()
        CraftingTableIIBlockEntityModel.register()
        CraftingTableIIBlockEntityRenderer.register()
        CraftingTableIISpecialRenderer.register()
    }
}
