package net.johnpgr.craftingtableiifabric.renderer

import com.mojang.serialization.MapCodec
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.block.CraftingTableIIBlock
import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntity
import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntityModel
import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntityRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.LoadedEntityModels
import net.minecraft.client.render.item.model.special.SpecialModelRenderer
import net.minecraft.client.render.item.model.special.SpecialModelTypes
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.state.property.Properties
import net.minecraft.item.ModelTransformationMode
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

@Environment(EnvType.CLIENT)
class CraftingTableIISpecialRenderer : SpecialModelRenderer<Unit> {
    companion object {
        fun register() {
            SpecialModelTypes.ID_MAPPER.put(CraftingTableIIBlock.ID, Unbaked.MAP_CODEC)
        }
    }

    private val client: MinecraftClient = MinecraftClient.getInstance()

    private val blockEntity: CraftingTableIIBlockEntity = CraftingTableIIBlockEntity(
        BlockPos.ORIGIN, CraftingTableIIMod.BLOCK.defaultState.with(Properties.HORIZONTAL_FACING, Direction.SOUTH)
    )

    private val renderer = CraftingTableIIBlockEntityRenderer(
        BlockEntityRendererFactory.Context(
            client.blockEntityRenderDispatcher,
            client.blockRenderManager,
            client.itemModelManager,
            client.itemRenderer,
            client.entityRenderDispatcher,
            CraftingTableIIBlockEntityModel.loadedEntityModels,
            client.textRenderer
        )
    )

    override fun render(
        data: Unit?,
        modelTransformationMode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        glint: Boolean
    ) {
        matrices.push()

        // Adjust the rotation and scale of the block item in interfaces
        if (modelTransformationMode == ModelTransformationMode.GUI) {
            matrices.translate(0.5, 0.5, 0.5)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270f))
            matrices.translate(-0.5, -0.6, -0.5)
            matrices.scale(1.12f, 1.12f, 1.12f)
        }

        renderer.render(
            blockEntity,
            client.renderTickCounter.getTickDelta(true),
            matrices,
            vertexConsumers,
            light,
            overlay
        )

        matrices.pop()
    }

    override fun getData(stack: ItemStack) {
        // No data is needed for rendering this item
    }

    data class Unbaked(val texture: Identifier) : SpecialModelRenderer.Unbaked {
        companion object {
            val MAP_CODEC: MapCodec<Unbaked> = Identifier.CODEC.fieldOf("texture").xmap(
                ::Unbaked
            ) { it.texture }
        }

        override fun bake(entityModels: LoadedEntityModels): SpecialModelRenderer<*> {
            return CraftingTableIISpecialRenderer()
        }

        override fun getCodec(): MapCodec<out SpecialModelRenderer.Unbaked> {
            return MAP_CODEC
        }
    }
}