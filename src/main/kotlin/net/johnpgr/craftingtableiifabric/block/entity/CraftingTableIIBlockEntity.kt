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
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CraftingTableIIBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : BlockEntity(CraftingTableIIMod.ENTITY_TYPE, pos, state), Inventory {
    private var inventory =
        DefaultedList.ofSize(CraftingTableIIInventory.SIZE, ItemStack.EMPTY)
    private var doorState = DoorState.CLOSED
    var doorAngle = 0.0f

    companion object {
        fun register() {
            Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                CraftingTableIIBlock.ID,
                CraftingTableIIMod.ENTITY_TYPE,
            )
        }

        private const val OPEN_SPEED = 0.2f

        fun tick(
            world: World,
            pos: BlockPos,
            state: BlockState,
            entity: CraftingTableIIBlockEntity
        ) {
            val player = world.getClosestPlayer(
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                10.0,
                false
            ) ?: return

            val playerDistance = player.squaredDistanceTo(
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble()
            )

            if (playerDistance < 7.0) {
                entity.doorAngle += OPEN_SPEED
                if (entity.doorAngle > 1.8f) entity.doorAngle = 1.8f

                if (entity.doorState != DoorState.OPEN) {
                    entity.doorState = DoorState.OPEN
                    world.playSound(
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble(),
                        SoundEvents.BLOCK_CHEST_OPEN,
                        SoundCategory.BLOCKS,
                        0.2f,
                        world.random.nextFloat() * 0.1f + 0.2f,
                        false
                    )
                }
            } else if (playerDistance > 7.0) {
                entity.doorAngle -= OPEN_SPEED
                if (entity.doorAngle < 0f) entity.doorAngle = 0f

                if (entity.doorState != DoorState.CLOSED) {
                    entity.doorState = DoorState.CLOSED
                    world.playSound(
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble(),
                        SoundEvents.BLOCK_CHEST_CLOSE,
                        SoundCategory.BLOCKS,
                        0.2f,
                        world.random.nextFloat() * 0.1f + 0.2f,
                        false
                    )
                }
            }
        }

    }

    override fun readNbt(
        nbt: NbtCompound?,
        registryLookup: RegistryWrapper.WrapperLookup?
    ) {
        super.readNbt(nbt, registryLookup)
        Inventories.readNbt(nbt, inventory, registryLookup)
    }

    override fun writeNbt(
        nbt: NbtCompound?,
        registryLookup: RegistryWrapper.WrapperLookup?
    ) {
        super.writeNbt(nbt, registryLookup)
        Inventories.writeNbt(nbt, inventory, registryLookup)
    }

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

    enum class DoorState {
        OPEN,
        CLOSED
    }
}
