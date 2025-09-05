package net.johnpgr.craftingtableiifabric.util

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

class BlockScreenHandlerFactory<T : ScreenHandler, B : BlockEntity>(
    val block: Block,
    val pos: BlockPos,
    val consumer: (Int, PlayerInventory, B, ScreenHandlerContext) -> T
) : ExtendedScreenHandlerFactory<BlockPos> {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T : ScreenHandler, B : BlockEntity> createHandlerType(
            consumer: (Int, PlayerInventory, B, ScreenHandlerContext) -> T
        ): ExtendedScreenHandlerType<T, BlockPos> {
            return ExtendedScreenHandlerType({ syncId, playerInventory, pos ->
                val player = playerInventory.player
                val world = player.world
                val blockEntity = world.getBlockEntity(pos) as B
                consumer.invoke(
                    syncId,
                    playerInventory,
                    blockEntity,
                    ScreenHandlerContext.create(world, pos)
                )
            }, BlockPos.PACKET_CODEC)
        }
    }

    private val displayName: Text = Text.translatable(
        "screen.${CraftingTableIIMod.MOD_ID}.${
            Registries.BLOCK.getId(block).path
        }"
    )

    @Suppress("UNCHECKED_CAST")
    override fun createMenu(
        syncId: Int,
        playerInv: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler {
        val world = player.world
        val blockEntity = world.getBlockEntity(pos) as B
        return consumer.invoke(
            syncId,
            playerInv,
            blockEntity,
            ScreenHandlerContext.create(world, pos)
        )
    }

    override fun getScreenOpeningData(player: ServerPlayerEntity?): BlockPos {
        return pos
    }

    override fun getDisplayName() = displayName
}