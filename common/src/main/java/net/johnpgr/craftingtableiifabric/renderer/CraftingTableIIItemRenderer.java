//? if <1.21.4 {
package net.johnpgr.craftingtableiifabric.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CraftingTableIIItemRenderer extends BlockEntityWithoutLevelRenderer {
    public CraftingTableIIItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        CraftingTableIIEntity tableEntity = new CraftingTableIIEntity(
                BlockPos.ZERO,
                CraftingTableII.BLOCK.defaultBlockState()
        );
        Minecraft instance = Minecraft.getInstance();
        CraftingTableIIEntityRenderer renderer = new CraftingTableIIEntityRenderer(
                new BlockEntityRendererProvider.Context(
                        instance.getBlockEntityRenderDispatcher(),
                        instance.getBlockRenderer(),
                        instance.getItemRenderer(),
                        instance.getEntityRenderDispatcher(),
                        instance.getEntityModels(),
                        instance.font
                )
        );
        renderer.render(
                tableEntity,
                //? if <1.21.3 {
                instance.getTimer().getGameTimeDeltaPartialTick(true),
                //? } else {
                instance.getDeltaTracker().getGameTimeDeltaPartialTick(true),
                //? }
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );
    }
}
//? }
