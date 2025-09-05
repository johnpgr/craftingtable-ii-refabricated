package net.johnpgr.craftingtableiifabric.block.entity

import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.block.CraftingTableIIBlock
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CraftingTableIIBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : BlockEntity(CraftingTableIIMod.ENTITY_TYPE, pos, state), Inventory {
    companion object {
        private const val OPEN_SPEED = 0.2f

        fun register() {
            Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                CraftingTableIIBlock.ID,
                CraftingTableIIMod.ENTITY_TYPE,
            )
        }
    }

    enum class DoorState {
        OPEN,
        CLOSED
    }

    private var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(
        CraftingTableIIInventory.SIZE,
        ItemStack.EMPTY
    )
    private var doorState: DoorState = DoorState.CLOSED
    var doorAngle: Float = 0.0f

    override fun size(): Int {
        return inventory.size
    }

    override fun isEmpty(): Boolean {
        return inventory.all { it.isEmpty }
    }

    override fun getStack(slot: Int): ItemStack {
        return inventory[slot]
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        if (stack.count > stack.maxCount) {
            stack.count = stack.maxCount
        }

        inventory[slot] = stack
    }

    override fun removeStack(slot: Int, count: Int): ItemStack {
        return Inventories.splitStack(inventory, slot, count)
    }

    override fun removeStack(slot: Int): ItemStack {
        return Inventories.removeStack(inventory, slot)
    }

    override fun canTransferTo(
        hopperInventory: Inventory,
        slot: Int,
        stack: ItemStack
    ): Boolean {
        return false
    }

    override fun isValid(slot: Int, stack: ItemStack): Boolean {
        return false
    }

    override fun clear() {
        inventory.clear()
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        return true
    }

    fun tick() {
        val world: World = world ?: return
        val x = pos.x.toDouble()
        val y = pos.y.toDouble()
        val z = pos.z.toDouble()

        val player = world.getClosestPlayer(
            x, y, z,
            10.0,
            false
        ) ?: return

        val playerDistance = player.squaredDistanceTo(x, y, z)

        if (playerDistance < 7.0) {
            onPlayerApproach(player)
        } else if (playerDistance > 7.0) {
            onPlayerLeave(player)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onPlayerApproach(player: PlayerEntity) {
        val world: World = world ?: return

        doorAngle += OPEN_SPEED
        if (doorAngle > 1.8f) doorAngle = 1.8f

        if (doorState != DoorState.OPEN) {
            doorState = DoorState.OPEN

            world.playSound(
                null,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                SoundEvents.BLOCK_CHEST_OPEN,
                SoundCategory.BLOCKS,
                0.2f,
                world.random.nextFloat() * 0.1f + 0.2f,
            )
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onPlayerLeave(player: PlayerEntity) {
        val world: World = world ?: return

        doorAngle -= OPEN_SPEED
        if (doorAngle < 0f) doorAngle = 0f

        if (doorState != DoorState.CLOSED) {
            doorState = DoorState.CLOSED
            world.playSound(
                null,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                SoundEvents.BLOCK_CHEST_CLOSE,
                SoundCategory.BLOCKS,
                0.2f,
                world.random.nextFloat() * 0.1f + 0.2f,
            )
        }
    }
}
