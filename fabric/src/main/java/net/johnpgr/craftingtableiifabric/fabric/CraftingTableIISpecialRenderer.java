//? if >=1.21.4 {
package net.johnpgr.craftingtableiifabric.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.client.model.geom.EntityModelSet;

@Environment(EnvType.CLIENT)
public class CraftingTableIISpecialRenderer implements SpecialModelRenderer<Void> {
    private final Minecraft client = Minecraft.getInstance();
    private final CraftingTableIIEntity blockEntity = new CraftingTableIIEntity(
            BlockPos.ZERO,
            CraftingTableII.BLOCK.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
    );
    private final CraftingTableIIEntityRenderer renderer;

    public CraftingTableIISpecialRenderer() {
        this.renderer = new CraftingTableIIEntityRenderer(
                new BlockEntityRendererProvider.Context(
                        client.getBlockEntityRenderDispatcher(),
                        client.getBlockRenderer(),
                        client.getItemRenderer(),
                        client.getEntityRenderDispatcher(),
                        client.getEntityModels(),
                        client.font
                )
        );
    }

    public static void register() {
        SpecialModelRenderers.ID_MAPPER.put(CraftingTableII.id("crafting_table_ii"), Unbaked.MAP_CODEC);
    }

    @Override
    public void render(
            Void data,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            boolean glint
    ) {
        poseStack.pushPose();

        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(270f));
            poseStack.translate(-0.5, -0.6, -0.5);
            poseStack.scale(1.12f, 1.12f, 1.12f);
        }

        renderer.render(
                blockEntity,
                //? if <1.21.6 {
                client.getTimer().getGameTimeDeltaPartialTick(true),
                //? } else {
                client.getDeltaTracker().getGameTimeDeltaPartialTick(true),
                //? }
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }

    @Override
    public void getExtentsForGui(ItemStack stack) {
        // No custom GUI extents needed.
    }

    public record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = ResourceLocation.CODEC.fieldOf("texture")
                .xmap(Unbaked::new, Unbaked::texture);

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet entityModels) {
            return new CraftingTableIISpecialRenderer();
        }
    }
}
//? }
