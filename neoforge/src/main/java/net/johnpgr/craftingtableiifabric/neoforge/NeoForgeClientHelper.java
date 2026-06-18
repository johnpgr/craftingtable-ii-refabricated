package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityModel;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer;
import net.johnpgr.craftingtableiifabric.platform.services.IClientHelper;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class NeoForgeClientHelper implements IClientHelper {
    @Override
    public void bootstrap() {
        // Client registration is handled through NeoForge events.
    }

    @Override
    public void onClientStarted(Runnable callback) {
        // Deferred until the player joins a world and client resources are available.
    }

    @EventBusSubscriber(modid = CraftingTableII.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class GameClientEvents {
        @SubscribeEvent
        public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
            CraftingTableIIDescriptions.ensureLoaded(Minecraft.getInstance());
        }
    }

    @EventBusSubscriber(modid = CraftingTableII.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                CraftingTableII.initClient();
                MenuScreens.register(CraftingTableII.MENU_TYPE, CraftingTableIIScreen::new);
            });
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(CraftingTableII.ENTITY_TYPE, CraftingTableIIEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            CraftingTableIIEntityModel.getLayerDefinitions().forEach(event::registerLayerDefinition);
        }
    }
}
