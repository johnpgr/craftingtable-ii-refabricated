package net.johnpgr.craftingtableiifabric;

import net.johnpgr.craftingtableiifabric.neoforge.CraftingTableIINeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CraftingTableII.MOD_ID)
public class CraftingTableIIMod {
    public CraftingTableIIMod(IEventBus modBus) {
        CraftingTableIINeoForge.init(modBus);
    }
}
