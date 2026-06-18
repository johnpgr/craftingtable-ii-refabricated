package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;

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
