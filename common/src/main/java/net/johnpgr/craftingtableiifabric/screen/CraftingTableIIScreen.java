package net.johnpgr.craftingtableiifabric.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions;
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory;
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIISlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class CraftingTableIIScreen extends AbstractContainerScreen<CraftingTableIIScreenHandler> {
    private static final ResourceLocation TEXTURE = CraftingTableII.id("textures/gui/crafttableii.png");
    private static final ResourceLocation DESCRIPTION_TEXTURE = CraftingTableII.id("textures/gui/crafttableii_description.png");

    private boolean scrolling = false;
    private float scrollPosition = 0.0f;

    public CraftingTableIIScreen(CraftingTableIIScreenHandler menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    private int getScrollBarStartY() {
        return this.topPos + 17;
    }

    private int getScrollBarEndY() {
        return this.getScrollBarStartY() + 90;
    }

    private int getScrollButtonY() {
        int start = this.getScrollBarStartY();
        int end = this.getScrollBarEndY();
        return this.topPos + 17 + (int) ((end - start - 17) * scrollPosition);
    }

    private int getScrollButtonX() {
        return this.leftPos + 154;
    }

    private boolean hasScrollbar() {
        return this.menu.recipeManager.results.size() > CraftingTableIIInventory.SIZE;
    }

    private boolean isClickInScrollbar(double mouseX, double mouseY) {
        return mouseX >= this.getScrollButtonX() && mouseX <= this.getScrollButtonX() + 16
                && mouseY >= this.getScrollBarStartY() && mouseY <= this.getScrollBarEndY();
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null) {
            CraftingTableIIDescriptions.ensureLoaded(this.minecraft);
        }
        this.imageHeight = 208;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 11;
        this.inventoryLabelY = this.imageHeight - 97;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.scrolling) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        int start = this.getScrollBarStartY();
        int end = this.getScrollBarEndY();
        this.scrollPosition = (float) ((mouseY - start - 7.5f) / ((end - start) - 15.0f));
        this.scrollPosition = Mth.clamp(this.scrollPosition, 0f, 1f);
        scrollResults(scrollPosition);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.scrolling || button != 0 || !this.isClickInScrollbar(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        this.scrolling = this.hasScrollbar();
        return true;
    }

    private void scrollResults(float scrollPos) {
        var results = menu.recipeManager.results;
        int i = (results.size() + 8 - 1) / 8 - 5;
        int j = (int) (scrollPos * i + 0.5);
        if (j < 0) {
            j = 0;
        }

        int listIndex = j * 8;
        if (listIndex < results.size()) {
            this.menu.currentListIndex = listIndex;
        }

        this.menu.updateRecipes(false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int craftableRecipesSize = this.menu.recipeManager.results.size();
        if (craftableRecipesSize <= CraftingTableIIInventory.SIZE) {
            return false;
        }

        double aX = mouseX - this.leftPos;
        double aY = mouseY - this.topPos;

        if (aX >= 0 && aY >= 0 && aX < 176 && aY < this.imageHeight - 100) {
            double i = (craftableRecipesSize + 8 - 1) / 8.0 - 5.0;
            double j = Mth.clamp(scrollY, -1.0, 1.0);

            scrollPosition -= (float) (j / i);
            scrollPosition = Mth.clamp(scrollPosition, 0f, 1f);
            scrollResults(scrollPosition);

            return true;
        }

        return false;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        menu.tick();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int craftableRecipesSize = this.menu.recipeManager.results.size();

        graphics.blit(
                TEXTURE,
                this.getScrollButtonX(),
                this.getScrollButtonY(),
                craftableRecipesSize <= CraftingTableIIInventory.SIZE ? 16 : 0,
                208,
                16,
                16
        );

        for (int i = CraftingTableIIScreenHandler.CTII_INVENTORY_INDEX_START; i <= CraftingTableIIScreenHandler.CTII_INVENTORY_INDEX_END; i++) {
            if (!(menu.getSlot(i) instanceof CraftingTableIISlot slot)) {
                continue;
            }

            if (!isMouseOverSlot(slot, mouseX, mouseY)) {
                continue;
            }

            if (!slot.hasItem()) {
                continue;
            }

            graphics.blit(DESCRIPTION_TEXTURE, leftPos - 124, topPos, 0, 0, 121, 162);

            var recipe = slot.getRecipe();
            if (recipe == null) {
                continue;
            }

            List<ItemStack> ingredientStacks = new ArrayList<>();

            for (Ingredient ingredient : recipe.value().getIngredients()) {
                if (ingredient.isEmpty()) {
                    continue;
                }

                ItemStack item = ingredient.getItems()[0];
                int index = -1;
                for (int j = 0; j < ingredientStacks.size(); j++) {
                    if (ItemStack.isSameItem(ingredientStacks.get(j), item)) {
                        index = j;
                        break;
                    }
                }

                if (index == -1) {
                    ingredientStacks.add(item.copy());
                } else {
                    ingredientStacks.get(index).grow(item.getCount());
                }
            }

            for (int r = 0; r < ingredientStacks.size(); r++) {
                ItemStack stack = ingredientStacks.get(r);
                graphics.renderItem(stack, leftPos - 25, topPos + 5 + r * 18);
                graphics.renderItemDecorations(this.font, stack, leftPos - 25, topPos + 5 + r * 18);
            }

            ItemStack output = recipe.value().getResultItem(this.minecraft.level.registryAccess());

            int titleX = leftPos - 118;
            int titleY = topPos + 9;

            String titleText = output.getHoverName().getString();
            if (titleText.length() > 16) {
                titleText = titleText.substring(0, 16) + "...";
            }

            graphics.drawString(this.font, titleText, titleX, titleY, 0xFFFFFF, false);

            String description = CraftingTableIIDescriptions.descriptionsDict.getOrDefault(
                    output.getDescriptionId(), ""
            );
            List<String> chunks = chunkDescription(description);
            int descY = titleY + 2;
            float scale = 0.5f;

            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.pose().translate(titleX / scale, descY / scale, 0.0);

            for (int index = 0; index < chunks.size(); index++) {
                graphics.drawString(this.font, chunks.get(index), 0, 40 + 10 * index, 0xFFFFFF, false);
            }

            graphics.drawString(this.font, "Code name: ", 0, 268, 0xFFFFFF, false);
            graphics.drawString(this.font, output.getItem().toString(), 0, 280, 0xFFFFFF, false);

            graphics.pose().popPose();
        }
    }

    private boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY) {
        int aX = mouseX - leftPos;
        int aY = mouseY - topPos;
        return aX >= slot.x && aX < slot.x + 18 && aY >= slot.y && aY < slot.y + 18;
    }

    private List<String> chunkDescription(String description) {
        if (description.isEmpty()) {
            return List.of("");
        }

        List<String> chunks = new ArrayList<>();
        String[] sentences = description.split("\\. ");

        for (String sentence : sentences) {
            String[] words = sentence.split(" ");
            StringBuilder chunk = new StringBuilder();

            for (String word : words) {
                if (chunk.length() + word.length() + 1 > 36) {
                    chunks.add(chunk.toString().trim());
                    chunk = new StringBuilder();
                }
                chunk.append(word).append(' ');
            }

            if (!chunk.isEmpty()) {
                chunks.add(chunk.toString().trim() + ".");
            }
        }

        if (!chunks.isEmpty()) {
            String last = chunks.get(chunks.size() - 1);
            chunks.set(chunks.size() - 1, last.substring(0, last.length() - 1));
        }

        return chunks;
    }
}
