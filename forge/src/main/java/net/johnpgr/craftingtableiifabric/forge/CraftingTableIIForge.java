package net.johnpgr.craftingtableiifabric.forge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

public final class CraftingTableIIForge {
    private CraftingTableIIForge() {}

    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        CraftingTableIIRegisters.register(modBus);
        modBus.addListener(CraftingTableIIForge::commonSetup);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CraftingTableII::init);
    }

    public static File getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().toFile();
    }
}
