package net.johnpgr.craftingtableiifabric.forge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CraftingTableII.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeModEvents {
    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(CraftingTableIIRegisters.CRAFTING_TABLE_ITEM);
        }
    }
}
