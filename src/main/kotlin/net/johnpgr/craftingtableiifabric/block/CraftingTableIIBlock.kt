package net.johnpgr.craftingtableiifabric.block

import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntity
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.johnpgr.craftingtableiifabric.util.BlockScreenHandlerFactory
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class CraftingTableIIBlock : BlockWithEntity(
    Settings.copy(Blocks.CRAFTING_TABLE).registryKey(REGISTRY_KEY)
) {
    companion object {
        val ID = CraftingTableIIMod.id("crafting_table_ii")
        val REGISTRY_KEY: RegistryKey<Block> =
            RegistryKey.of(RegistryKeys.BLOCK, ID)
        val ITEM_REGISTRY_KEY: RegistryKey<Item> =
            RegistryKey.of(RegistryKeys.ITEM, ID)
        val CODEC: MapCodec<CraftingTableIIBlock> =
            createCodec { CraftingTableIIBlock() }

        fun register() {
            Registry.register(Registries.BLOCK, ID, CraftingTableIIMod.BLOCK)

            Registry.register(
                Registries.ITEM, ITEM_REGISTRY_KEY, BlockItem(
                    CraftingTableIIMod.BLOCK,
                    Item.Settings().useBlockPrefixedTranslationKey()
                        .registryKey(ITEM_REGISTRY_KEY)
                )
            )

            ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register { content ->
                    content.addAfter(
                        Items.CRAFTING_TABLE, CraftingTableIIMod.BLOCK
                    )
                }
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.HORIZONTAL_FACING)
    }

    // FIXME: Maybe this is an unnecessary hack
    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        var direction = ctx.horizontalPlayerFacing.rotateYCounterclockwise()
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            direction = direction.opposite
        }

        return defaultState.with(Properties.HORIZONTAL_FACING, direction)
    }

    override fun rotate(
        state: BlockState,
        rotation: BlockRotation
    ): BlockState {
        return state.with(
            Properties.HORIZONTAL_FACING,
            rotation.rotate(state[Properties.HORIZONTAL_FACING])
        )
    }

    override fun mirror(
        state: BlockState,
        mirror: BlockMirror
    ): BlockState {
        return state.rotate(mirror.getRotation(state[Properties.HORIZONTAL_FACING]))
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun hasSidedTransparency(state: BlockState): Boolean {
        return true
    }

    override fun getCollisionShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return createCuboidShape(1.0, 0.0, 2.0, 15.0, 16.0, 15.0)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return createCuboidShape(1.0, 0.0, 2.0, 15.0, 16.0, 15.0)
    }

    override fun createBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity {
        return CraftingTableIIBlockEntity(pos, state)
    }

    override fun getCodec(): MapCodec<out BlockWithEntity> {
        return CODEC
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }

        player.openHandledScreen(
            BlockScreenHandlerFactory(this, pos, ::CraftingTableIIScreenHandler)
        )

        return ActionResult.CONSUME
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return validateTicker(
            type,
            CraftingTableIIMod.ENTITY_TYPE
        ) { world1, pos, state1, entity -> entity.tick() }!!
    }

    override fun hasComparatorOutput(state: BlockState): Boolean {
        return false
    }
}