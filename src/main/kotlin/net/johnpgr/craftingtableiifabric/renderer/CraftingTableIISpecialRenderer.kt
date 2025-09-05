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
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
import net.minecraft.client.render.item.model.special.SpecialModelRenderer
import net.minecraft.client.render.item.model.special.SpecialModelTypes
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemDisplayContext
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import org.joml.Vector3f

@Environment(EnvType.CLIENT)
class CraftingTableIISpecialRenderer : SimpleSpecialModelRenderer {
    companion object {
        fun register() {
            SpecialModelTypes.ID_MAPPER.put(
                CraftingTableIIBlock.ID,
                Unbaked.MAP_CODEC
            )
        }
    }

    private val client: MinecraftClient = MinecraftClient.getInstance()

    private val blockEntity: CraftingTableIIBlockEntity =
        CraftingTableIIBlockEntity(
            BlockPos.ORIGIN,
            CraftingTableIIMod.BLOCK.defaultState.with(
                Properties.HORIZONTAL_FACING,
                Direction.SOUTH
            )
        )

    private val renderer: CraftingTableIIBlockEntityRenderer =
        CraftingTableIIBlockEntityRenderer(
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
        displayContext: ItemDisplayContext,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        glint: Boolean
    ) {
        val tickProgress = client.renderTickCounter.getTickProgress(true)
        val cameraVec3d = client.gameRenderer.camera.pos

        when (displayContext) {
            ItemDisplayContext.GUI -> {
                matrices.push()

                matrices.translate(0.5, 0.5, 0.5)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270f))
                matrices.translate(-0.5, -0.6, -0.5)
                matrices.scale(1.12f, 1.12f, 1.12f)


                renderer.render(
                    blockEntity,
                    tickProgress,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    cameraVec3d
                )

                matrices.pop()
            }

            else ->
                renderer.render(
                    blockEntity,
                    tickProgress,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    cameraVec3d
                )
        }
    }

    override fun collectVertices(vertices: Set<Vector3f>) {}

    data class Unbaked(val texture: Identifier) : SpecialModelRenderer.Unbaked {
        companion object {
            val MAP_CODEC: MapCodec<Unbaked> =
                Identifier.CODEC.fieldOf("texture").xmap(
                    ::Unbaked
                ) { it.texture }
        }

        override fun bake(entityModels: LoadedEntityModels): SpecialModelRenderer<*> =
            CraftingTableIISpecialRenderer()

        override fun getCodec(): MapCodec<out SpecialModelRenderer.Unbaked> =
            MAP_CODEC
    }
}