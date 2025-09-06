package net.johnpgr.craftingtableiifabric.screen

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.block.CraftingTableIIBlock
import net.johnpgr.craftingtableiifabric.block.entity.CraftingTableIIBlockEntity
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIISlot
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPacket
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.book.RecipeBookType
import net.minecraft.recipe.display.RecipeDisplay
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.AbstractCraftingScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

// FIXME: Scrolling mouse on a resultInventory's slot when mouse wheel tweaks from mods are enabled cause ConcurrentModificationException
// FIXME: Inventory Profiles Next functions to this screen's inventory cause ConcurrentModificationException also
class CraftingTableIIScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    entity: CraftingTableIIBlockEntity,
    private val context: ScreenHandlerContext,
) : AbstractCraftingScreenHandler(
    CraftingTableIIMod.SCREEN_HANDLER,
    syncId,
    3,
    3,
) {
    companion object {
        fun register() {
            Registry.register(
                Registries.SCREEN_HANDLER,
                CraftingTableIIBlock.ID,
                CraftingTableIIMod.SCREEN_HANDLER
            )
        }

        const val RESULT_INDEX = 0
//        const val INPUT_INDEX_START = 1
//        const val INPUT_INDEX_END = 9
//        const val PLAYER_INVENTORY_INDEX_START = 9
//        const val PLAYER_INVENTORY_INDEX_END = 36
//        const val PLAYER_HOTBAR_INDEX_START = 36
//        const val PLAYER_HOTBAR_INDEX_END = 45
        const val CTII_INVENTORY_INDEX_START = 45
        const val CTII_INVENTORY_INDEX_END = 85
    }

    var currentListIndex = 0
    val input = CraftingInventory(this, 3, 3)
    val result = CraftingResultInventory()
    val inventory = CraftingTableIIInventory(entity)
    lateinit var recipeManager: CraftingTableIIRecipeManager
    private val player = playerInventory.player
    private var lastCraftedItem = ItemStack.EMPTY
    private var cachedInvChangeCount = -1

    init {
        // The Crafting Result
        addSlot(
            CraftingResultSlot(
                player, input, result, 0, -999, -999
            )
        )

        // The Crafting Grid
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                addSlot(Slot(input, col + row * 3, -999, -999))
            }
        }

        // The player inventory
        for (row in 0 until 3) {
            for (col in 0 until 9) {
                addSlot(
                    Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        125 + row * 18
                    )
                )
            }
        }

        // The player hotbar
        for (row in 0 until 9) {
            addSlot(Slot(playerInventory, row, 8 + row * 18, 184))
        }

        // Our inventory
        for (row in 0 until CraftingTableIIInventory.ROWS) {
            for (col in 0 until CraftingTableIIInventory.COLS) {
                addSlot(
                    CraftingTableIISlot(
                        inventory,
                        col + row * CraftingTableIIInventory.COLS,
                        8 + col * 18,
                        18 + row * 18
                    )
                )
            }
        }

        if (player.world.isClient) {
            recipeManager =
                CraftingTableIIRecipeManager(this, player as ClientPlayerEntity)
        }
    }

    fun tick() {
        if (cachedInvChangeCount != player.inventory.changeCount) {
            cachedInvChangeCount = player.inventory.changeCount
            updateRecipes(true)
        }
    }

    private fun addRecipeItem(recipe: CraftingTableIIRecipeManager.Result) {
        for (i in 0 until inventory.size()) {
            if (inventory.getStack(i).isEmpty) {
                inventory.setStack(i, recipe)
                return
            }
        }
    }

    private fun isValidCTIISlot(index: Int): Boolean {
        return index >= 0 && index < slots.size
    }

    override fun onSlotClick(
        slotIndex: Int,
        button: Int,
        actionType: SlotActionType,
        player: PlayerEntity
    ) {
        super.onSlotClick(slotIndex, button, actionType, player)

        if (!player.world.isClient || !isValidCTIISlot(slotIndex) || button != 0) return

        val slot = getSlot(slotIndex) as? CraftingTableIISlot ?: return
        if (!slot.hasStack()) return

        val quickCraft = actionType == SlotActionType.QUICK_MOVE
        val itemStack = slot.stack
        val recipe = slot.recipe ?: return

        ClientPlayNetworking.send(
            CraftingTableIIPacket(
                recipe.id,
                syncId,
                quickCraft
            )
        )

        lastCraftedItem = itemStack
    }

    private fun validateLastCrafted(results: List<CraftingTableIIRecipeManager.Result>): CraftingTableIIRecipeManager.Result? {
        results.forEach { result ->
            val itemStack = result.getDisplayStack()
            if (ItemStack.areItemsEqual(itemStack, lastCraftedItem)) {
                return result
            }
        }
        return null
    }

    fun updateRecipes(shouldRefreshInputs: Boolean) {
        inventory.clear()
        if (shouldRefreshInputs) recipeManager.refreshInputs()

        val newList = mutableListOf<CraftingTableIIRecipeManager.Result>()

        validateLastCrafted(recipeManager.results)?.let {
            newList.add(it)
        } ?: run {
            lastCraftedItem = ItemStack.EMPTY
        }

        val max = CraftingTableIIInventory.SIZE - newList.size

        for (i in currentListIndex until currentListIndex + max) {
            recipeManager.results.getOrNull(i)?.let {
                newList.add(it)
            } ?: break
        }

        newList.forEach { addRecipeItem(it) }
    }

    override fun canInsertIntoSlot(slot: Slot?): Boolean {
        return slot !is CraftingTableIISlot
    }

    /**
     * Checks if the player can use the crafting table.
     * This method verifies if the block at the given position is the crafting table block
     * and if the player is within a 64-block radius of the block.
     */
    override fun canUse(player: PlayerEntity): Boolean {
        return context.get({ world: World, blockPos: BlockPos ->
            if (world.getBlockState(
                    blockPos
                ).block != CraftingTableIIMod.BLOCK
            ) false else player.squaredDistanceTo(
                blockPos.x + .5, blockPos.y + .5, blockPos.z + .5
            ) < 64.0
        }, true)
    }

    fun clearCraftingSlots() {
        input.clear()
        result.clear()
    }

    fun updateResultSlot(itemStack: ItemStack) {
        result.setStack(0, itemStack)
    }

    override fun getCategory(): RecipeBookType {
        return RecipeBookType.CRAFTING
    }

    override fun getOutputSlot(): Slot {
        return getSlot(0)
    }

    override fun getInputSlots(): MutableList<Slot> {
        return slots.subList(1, 10)
    }

    override fun getPlayer(): PlayerEntity {
        return player
    }

    override fun quickMove(
        playerEntity: PlayerEntity, invSlot: Int
    ): ItemStack {
        return ItemStack.EMPTY
    }

    fun canDisplay(display: RecipeDisplay): Boolean {
        return when (display) {
            is ShapedCraftingRecipeDisplay -> width >= display.width() && height >= display.height()
            is ShapelessCraftingRecipeDisplay -> width * height >= display.ingredients().size
            else -> false
        }
    }
}