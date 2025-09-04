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
import net.minecraft.item.ModelTransformationMode
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

@Environment(EnvType.CLIENT)
class CraftingTableIISpecialRenderer : SpecialModelRenderer<Unit> {
    companion object {
        fun register() {
            SpecialModelTypes.ID_MAPPER.put(CraftingTableIIBlock.ID, Unbaked.MAP_CODEC);
        }
    }

    override fun render(
        data: Unit?,
        modelTransformationMode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        glint: Boolean
    ) {
        val tableEntity = CraftingTableIIBlockEntity(BlockPos.ORIGIN, CraftingTableIIMod.BLOCK.defaultState)
        val instance = MinecraftClient.getInstance()

        val dummyRenderer = CraftingTableIIBlockEntityRenderer(
            BlockEntityRendererFactory.Context(
                instance.blockEntityRenderDispatcher,
                instance.blockRenderManager,
                instance.itemModelManager,
                instance.itemRenderer,
                instance.entityRenderDispatcher,
                CraftingTableIIBlockEntityModel.loadedEntityModels,
                instance.textRenderer
            )
        )
        dummyRenderer.render(
            tableEntity,
            instance.renderTickCounter.getTickDelta(true),
            matrices,
            vertexConsumers,
            light,
            overlay
        )
    }

    override fun getData(stack: ItemStack): Unit? {
        // No data is needed for rendering this item
        return Unit
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