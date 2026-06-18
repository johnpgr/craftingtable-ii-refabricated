package net.johnpgr.craftingtableiifabric;

import net.johnpgr.craftingtableiifabric.forge.CraftingTableIIForge;
import net.minecraftforge.fml.common.Mod;

@Mod(CraftingTableII.MOD_ID)
public class CraftingTableIIMod {
    public CraftingTableIIMod() {
        CraftingTableIIForge.init();
    }
}
