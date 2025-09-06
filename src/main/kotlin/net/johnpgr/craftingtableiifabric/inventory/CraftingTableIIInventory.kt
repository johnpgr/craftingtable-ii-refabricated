package net.johnpgr.craftingtableiifabric.inventory

import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntity
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class CraftingTableIIInventory(
    val entity: CraftingTableIIBlockEntity,
) : Inventory {
    companion object {
        const val COLS = 8
        const val ROWS = 5
        const val SIZE = ROWS * COLS
    }

    val recipes: MutableList<CraftingTableIIRecipeManager.Result> =
        mutableListOf()

    override fun size(): Int {
        return entity.size()
    }

    override fun isEmpty(): Boolean {
        return entity.isEmpty
    }

    override fun getStack(slot: Int): ItemStack {
        return entity.getStack(slot)
    }

    override fun removeStack(slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        if (stack.isEmpty) return

        entity.setStack(slot, stack.copy())
    }

    fun setStack(slot: Int, recipe: CraftingTableIIRecipeManager.Result) {
        setStack(slot, recipe.getDisplayStack())
        recipes.add(slot, recipe)
    }

    override fun markDirty() {
        entity.markDirty()
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        return entity.canPlayerUse(player)
    }

    override fun clear() {
        entity.clear()
        recipes.clear()
    }
}