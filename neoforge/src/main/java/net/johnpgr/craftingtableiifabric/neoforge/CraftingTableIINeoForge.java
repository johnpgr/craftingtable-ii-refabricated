package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;

public final class CraftingTableIINeoForge {
    private CraftingTableIINeoForge() {
    }

    public static void init(IEventBus modBus) {
        CraftingTableIIRegisters.register(modBus);
        modBus.addListener(CraftingTableIINeoForge::commonSetup);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CraftingTableII::init);
    }

    public static File getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().toFile();
    }
}
