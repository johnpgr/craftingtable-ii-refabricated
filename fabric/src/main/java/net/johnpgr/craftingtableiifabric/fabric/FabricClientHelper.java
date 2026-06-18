package net.johnpgr.craftingtableiifabric.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityModel;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer;
import net.johnpgr.craftingtableiifabric.platform.services.IClientHelper;
import net.johnpgr.craftingtableiifabric.renderer.CraftingTableIIItemRenderer;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.gui.screens.MenuScreens;

public class FabricClientHelper implements IClientHelper {
    private static CraftingTableIIItemRenderer itemRenderer;

    @Override
    public void bootstrap() {
        CraftingTableIIEntityModel.getLayerDefinitions().forEach(
                (layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get)
        );

        BlockEntityRenderers.register(CraftingTableII.ENTITY_TYPE, CraftingTableIIEntityRenderer::new);

        Minecraft client = Minecraft.getInstance();
        itemRenderer = new CraftingTableIIItemRenderer(
                client.getBlockEntityRenderDispatcher(),
                client.getEntityModels()
        );
        BuiltinItemRendererRegistry.INSTANCE.register(CraftingTableII.BLOCK, (stack, mode, matrices, vertexConsumers, light, overlay) ->
                itemRenderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay)
        );

        MenuScreens.register(CraftingTableII.MENU_TYPE, CraftingTableIIScreen::new);
    }

    @Override
    public void onClientStarted(Runnable callback) {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> callback.run());
    }
}
