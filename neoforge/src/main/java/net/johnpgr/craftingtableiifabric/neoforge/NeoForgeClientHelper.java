package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityModel;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer;
import net.johnpgr.craftingtableiifabric.platform.services.IClientHelper;
import net.johnpgr.craftingtableiifabric.renderer.CraftingTableIIItemRenderer;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.jetbrains.annotations.NotNull;

public class NeoForgeClientHelper implements IClientHelper {
    private static CraftingTableIIItemRenderer itemRenderer;

    @Override
    public void bootstrap() {
        // Client registration is handled through NeoForge events.
    }

    @Override
    public void onClientStarted(Runnable callback) {
        // Deferred until the player joins a world and client resources are available.
    }

    @EventBusSubscriber(modid = CraftingTableII.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
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
            event.enqueueWork(CraftingTableII::initClient);
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(CraftingTableII.MENU_TYPE, CraftingTableIIScreen::new);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(CraftingTableII.ENTITY_TYPE, CraftingTableIIEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            CraftingTableIIEntityModel.getLayerDefinitions().forEach(event::registerLayerDefinition);
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            Minecraft client = Minecraft.getInstance();
            itemRenderer = new CraftingTableIIItemRenderer(
                    client.getBlockEntityRenderDispatcher(),
                    client.getEntityModels()
            );
            event.registerItem(new IClientItemExtensions() {
                @Override @NotNull
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return itemRenderer;
                }
            }, CraftingTableIIRegisters.CRAFTING_TABLE_ITEM.get());
        }
    }
}
