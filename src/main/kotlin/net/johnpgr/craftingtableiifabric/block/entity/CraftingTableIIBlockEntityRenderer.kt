package net.johnpgr.craftingtableiifabric.block.entity

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

@Environment(EnvType.CLIENT)
class CraftingTableIIBlockEntityRenderer(arg: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<CraftingTableIIBlockEntity> {
    companion object {
        fun register() {
            BlockEntityRendererFactories.register(CraftingTableIIMod.ENTITY_TYPE) {
                CraftingTableIIBlockEntityRenderer(it)
            }
        }
    }

    @Suppress("DEPRECATION")
    private val texture = SpriteIdentifier(
        SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
        CraftingTableIIMod.id("block/craftingtableii")
    )
    private val table =
        arg.getLayerModelPart(CraftingTableIIBlockEntityModel.tableModelLayer)
    private val door =
        arg.getLayerModelPart(CraftingTableIIBlockEntityModel.doorModelLayer)
    private val doorSide =
        arg.getLayerModelPart(CraftingTableIIBlockEntityModel.doorSideModelLayer)
    private val doorSide1 =
        arg.getLayerModelPart(CraftingTableIIBlockEntityModel.doorSide1ModelLayer)
    private val doorTopSide =
        arg.getLayerModelPart(CraftingTableIIBlockEntityModel.doorTopSideModelLayer)
    private val doorTopSide1 =
        arg.getLayerModelPart(CraftingTableIIBlockEntityModel.doorTopSide1ModelLayer)
    private val book =
        arg.getLayerModelPart(CraftingTableIIBlockEntityModel.bookModelLayer)

    override fun render(
        entity: CraftingTableIIBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val consumer = texture.getVertexConsumer(
            vertexConsumers,
            RenderLayer::getEntityCutout
        )
        val blockState =
            if (entity.hasWorld()) entity.cachedState
            else (CraftingTableIIMod.BLOCK.defaultState.with(
                Properties.HORIZONTAL_FACING, Direction.SOUTH
            ))
        val lightAbove =
            if (entity.hasWorld()) WorldRenderer.getLightmapCoordinates(
                entity.world,
                entity.cachedState,
                entity.pos.up()
            )
            else light

        val direction = blockState.get(Properties.HORIZONTAL_FACING)
        val degrees = direction.positiveHorizontalDegrees

        matrices.push()
        matrices.translate(0.5, 1.0, 0.5)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees))
        matrices.scale(-1f, -1f, 1f)

        this.renderModels(
            entity.doorAngle,
            matrices,
            consumer,
            lightAbove,
            overlay
        )

        matrices.pop()
    }

    private fun renderModels(
        rotation: Float,
        matrices: MatrixStack,
        consumer: VertexConsumer,
        light: Int,
        overlay: Int
    ) {
        this.door.getChild("door").setAngles(0f, rotation, 0f)
        this.table.render(matrices, consumer, light, overlay)
        this.door.render(matrices, consumer, light, overlay)
        this.doorSide.render(matrices, consumer, light, overlay)
        this.doorSide1.render(matrices, consumer, light, overlay)
        this.doorTopSide.render(matrices, consumer, light, overlay)
        this.doorTopSide1.render(matrices, consumer, light, overlay)
        this.book.render(matrices, consumer, light, overlay)
    }
}