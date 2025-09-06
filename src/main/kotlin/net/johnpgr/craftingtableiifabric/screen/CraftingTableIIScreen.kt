package net.johnpgr.craftingtableiifabric.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIISlot
import net.johnpgr.craftingtableiifabric.util.breakLines
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Environment(EnvType.CLIENT)
class CraftingTableIIScreen(
    screenHandler: CraftingTableIIScreenHandler,
    playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<CraftingTableIIScreenHandler>(
    screenHandler,
    playerInventory,
    title
) {
    companion object {
        private val TEXTURE: Identifier =
            CraftingTableIIMod.id("textures/gui/crafttableii.png")
        private val DESCRIPTION_TEXTURE: Identifier =
            CraftingTableIIMod.id("textures/gui/crafttableii_description.png")

        fun register() {
            HandledScreens.register(
                CraftingTableIIMod.SCREEN_HANDLER, ::CraftingTableIIScreen
            )
        }
    }

    private val scrollBtnWidth: Int = 16
    private val scrollBtnHeight: Int = 16

    private var scrolling: Boolean = false
    private var scrollPosition: Float = 0.0f

    private val scrollBarStartY: Int
        get() = y + 17

    private val scrollBarEndY: Int
        get() = scrollBarStartY + 90

    private val scrollBtnY: Int
        get() = scrollBarStartY + ((scrollBarEndY - scrollBarStartY - 17) * scrollPosition).toInt()

    private val scrollBtnX: Int
        get() = x + 154

    private val scrollable: Boolean
        get() = screenHandler.recipeManager.results.size > CraftingTableIIInventory.SIZE

    private fun inScrollbar(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= scrollBtnX && mouseX <= scrollBtnX + scrollBtnHeight &&
                mouseY >= scrollBarStartY && mouseY <= scrollBarEndY
    }

    override fun init() {
        super.init()
        backgroundHeight = 208
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 11
        playerInventoryTitleY = backgroundHeight - 97
        x = width / 2 - backgroundWidth / 2
        y = height / 2 - backgroundHeight / 2
    }

    override fun render(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
    ) {
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun mouseReleased(
        mouseX: Double,
        mouseY: Double,
        button: Int,
    ): Boolean {
        if (button == 0) {
            scrolling = false
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(
        mouseX: Double,
        mouseY: Double,
        button: Int,
        deltaX: Double,
        deltaY: Double,
    ): Boolean {
        if (!scrolling) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }

        val start = scrollBarStartY
        val end = scrollBarEndY
        scrollPosition =
            ((mouseY - start - 7.5f) / (end - start - 15.0f)).toFloat()
        scrollPosition = MathHelper.clamp(scrollPosition, 0f, 1f)

        scrollResults(scrollPosition)
        return true
    }

    override fun mouseClicked(
        mouseX: Double,
        mouseY: Double,
        button: Int,
    ): Boolean {
        if (scrollable
            && !scrolling
            && button == 0
            && inScrollbar(mouseX, mouseY)
        ) {
            scrolling = true
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    /**
     * Scrolls through the list of craftable recipes based on the given scroll position.
     * Updates the current list index in the crafting screen handler and refreshes the displayed recipes.
     */
    private fun scrollResults(scrollPos: Float) {
        val results = screenHandler.recipeManager.results
        val i = (results.size + 8 - 1) / 8 - 5f
        var j = ((scrollPos * i) + 0.5).toInt()
        if (j < 0) {
            j = 0
        }

        val listIndex = j * 8
        if (listIndex < results.size) {
            screenHandler.currentListIndex = listIndex
        }

        screenHandler.updateRecipes(false)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        amount: Double
    ): Boolean {
        val craftableRecipesSize = screenHandler.recipeManager.results.size
        if (craftableRecipesSize <= CraftingTableIIInventory.SIZE) {
            return false
        }

        val aX = mouseX - x
        val aY = mouseY - y

        // Check if the mouse is in our inventory bounds
        if ((aX >= 0 && aY >= 0 && aX < 176) && aY < backgroundHeight - 100) {
            val i = (craftableRecipesSize + 8 - 1) / 8 - 5
            val j = MathHelper.clamp(amount, -1.0, 1.0)

            scrollPosition -= (j / i).toFloat()
            scrollPosition = MathHelper.clamp(scrollPosition, 0f, 1f)
            scrollResults(scrollPosition)

            return true
        }

        return false
    }

    override fun handledScreenTick() {
        super.handledScreenTick()
        screenHandler.tick()
    }

    override fun drawBackground(
        ctx: DrawContext,
        delta: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        val client = client ?: return

        // Draw inventory
        ctx.drawTexture(
            RenderPipelines.GUI_TEXTURED, TEXTURE,
            x, y,
            0.0f, 0.0f,
            backgroundWidth, backgroundHeight,
            256, 256
        )

        val craftableRecipesSize = screenHandler.recipeManager.results.size
        val hasScroll = craftableRecipesSize > CraftingTableIIInventory.SIZE

        val scrollU = if (hasScroll) 0f else 16f
        val scrollV = 208f

        // Draw scrollbar
        ctx.drawTexture(
            RenderPipelines.GUI_TEXTURED, TEXTURE,
            scrollBtnX, scrollBtnY,
            scrollU, scrollV,
            scrollBtnWidth, scrollBtnHeight,
            256, 256
        )

        for (i in CraftingTableIIScreenHandler.CTII_INVENTORY_INDEX_START
                ..CraftingTableIIScreenHandler.CTII_INVENTORY_INDEX_END) {
            val slot =
                screenHandler.getSlot(i) as? CraftingTableIISlot ?: continue

            if (!isMouseOverSlot(slot, mouseX, mouseY)) continue
            if (slot.stack.isEmpty) continue

            //draw description overlay
            ctx.drawTexture(
                RenderPipelines.GUI_TEXTURED, DESCRIPTION_TEXTURE,
                x - 124, y,
                0f, 0f,
                121, 162,
                256, 256
            )

            val recipe = slot.recipe ?: continue
            val ingredientStacks = arrayListOf<ItemStack>()

            // TODO: Find a way to draw all matching stacks.
            // Maybe a timer that loops through the list of matching stacks
            for (ingredient in recipe.ingredients) {
                if (ingredient.isEmpty) continue

                @Suppress("DEPRECATION")
                val entry = ingredient.matchingItems.findFirst().orElse(null)
                    ?: continue

                val item = entry.value()
                val itemStack = item.defaultStack

                val index = ingredientStacks.indexOfFirst { it.item == item }

                if (index == -1) {
                    ingredientStacks.add(itemStack.copy())
                    continue
                }
                ingredientStacks[index].count += itemStack.count
            }

            for ((r, stack) in ingredientStacks.withIndex()) {
                ctx.drawItem(stack, x - 25, y + 5 + r * 18)
                ctx.drawStackOverlay(
                    client.textRenderer,
                    stack,
                    x - 25, y + 5 + r * 18
                )
            }

            val output = recipe.getDisplayStack()

            val titleX = x - 118
            val titleY = y + 9

            val title = if (output.name.string.length > 16) {
                output.name.string.substring(0, 16) + "..."
            } else {
                output.name.string
            }

            // Draw title
            ctx.drawText(
                client.textRenderer,
                title, titleX, titleY,
                0xFFFFFF, false,
            )

            val description =
                CraftingTableIIDescriptions.descriptions[output.item.translationKey]
                    ?: ""
            val chunks = description.breakLines()
            val descY = titleY + 2
            val scale = 0.5f

            ctx.matrices.pushMatrix()
            ctx.matrices.scale(scale)
            ctx.matrices.translate(titleX / scale, descY / scale)

            for ((index, text) in chunks.withIndex()) {
                val textX = 0
                val textY = 40 + index * 10
                ctx.drawText(
                    client.textRenderer,
                    text, textX, textY,
                    0xFFFFFF, false
                )
            }

            ctx.drawText(
                client.textRenderer,
                "Code name: ", 0, 268,
                0xFFFFFF, false
            )

            ctx.drawText(
                client.textRenderer,
                output.item.toString(), 0, 280,
                0xFFFFFF, false
            )

            ctx.matrices.popMatrix()
        }
    }

    private fun isMouseOverSlot(slot: Slot, mouseX: Int, mouseY: Int): Boolean {
        val aX = mouseX - x
        val aY = mouseY - y
        return aX >= slot.x && aX < slot.x + 18 && aY >= slot.y && aY < slot.y + 18
    }
}
