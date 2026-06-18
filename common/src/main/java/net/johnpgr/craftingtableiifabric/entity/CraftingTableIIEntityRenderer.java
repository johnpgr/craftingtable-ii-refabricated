package net.johnpgr.craftingtableiifabric.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CraftingTableIIEntityRenderer implements BlockEntityRenderer<CraftingTableIIEntity> {
    private static final Material TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, CraftingTableII.id("block/craftingtableii"));

    private final ModelPart table;
    private final ModelPart door;
    private final ModelPart doorSide;
    private final ModelPart doorSide1;
    private final ModelPart doorTopSide;
    private final ModelPart doorTopSide1;
    private final ModelPart book;

    public CraftingTableIIEntityRenderer(BlockEntityRendererProvider.Context context) {
        var modelSet = context.getModelSet();
        this.table = modelSet.bakeLayer(CraftingTableIIEntityModel.TABLE_MODEL_LAYER);
        this.door = modelSet.bakeLayer(CraftingTableIIEntityModel.DOOR_MODEL_LAYER);
        this.doorSide = modelSet.bakeLayer(CraftingTableIIEntityModel.DOOR_SIDE_MODEL_LAYER);
        this.doorSide1 = modelSet.bakeLayer(CraftingTableIIEntityModel.DOOR_SIDE1_MODEL_LAYER);
        this.doorTopSide = modelSet.bakeLayer(CraftingTableIIEntityModel.DOOR_TOP_SIDE_MODEL_LAYER);
        this.doorTopSide1 = modelSet.bakeLayer(CraftingTableIIEntityModel.DOOR_TOP_SIDE1_MODEL_LAYER);
        this.book = modelSet.bakeLayer(CraftingTableIIEntityModel.BOOK_MODEL_LAYER);
    }

    @Override
    public void render(
            CraftingTableIIEntity entity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        VertexConsumer consumer = TEXTURE.buffer(bufferSource, RenderType::entityCutout);

        BlockState blockState = entity.hasLevel()
                ? entity.getBlockState()
                : CraftingTableII.BLOCK.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);

        int lightAbove = entity.hasLevel()
                ? net.minecraft.client.renderer.LevelRenderer.getLightColor(
                        entity.getLevel(), blockState, entity.getBlockPos().above())
                : packedLight;

        float rotation = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() * 89f;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(-rotation));
        poseStack.scale(-1f, -1f, 1f);

        renderModels(entity.doorAngle, poseStack, consumer, lightAbove, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    private void renderModels(float rotation, PoseStack poseStack, VertexConsumer consumer, int light, int overlay) {
        this.door.getChild("door").setRotation(0f, rotation, 0f);
        this.table.render(poseStack, consumer, light, overlay);
        this.door.render(poseStack, consumer, light, overlay);
        this.doorSide.render(poseStack, consumer, light, overlay);
        this.doorSide1.render(poseStack, consumer, light, overlay);
        this.doorTopSide.render(poseStack, consumer, light, overlay);
        this.doorTopSide1.render(poseStack, consumer, light, overlay);
        this.book.render(poseStack, consumer, light, overlay);
    }
}
