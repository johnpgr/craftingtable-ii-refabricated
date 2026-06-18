package net.johnpgr.craftingtableiifabric;

import net.johnpgr.craftingtableiifabric.neoforge.CraftingTableIINeoForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

@Mod(CraftingTableII.MOD_ID)
public class CraftingTableIIMod {
    public CraftingTableIIMod(IEventBus modBus) {
        CraftingTableIINeoForge.init(modBus);
    }
}
