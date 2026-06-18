package net.johnpgr.craftingtableiifabric.screen;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory;
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIISlot;
import net.johnpgr.craftingtableiifabric.platform.Services;
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CraftingTableIIScreenHandler extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    public static final int RESULT_INDEX = 0;
    public static final int INPUT_INDEX_START = 1;
    public static final int INPUT_INDEX_END = 9;
    public static final int PLAYER_INVENTORY_INDEX_START = 9;
    public static final int PLAYER_INVENTORY_INDEX_END = 36;
    public static final int PLAYER_HOTBAR_INDEX_START = 36;
    public static final int PLAYER_HOTBAR_INDEX_END = 45;
    public static final int CTII_INVENTORY_INDEX_START = 45;
    public static final int CTII_INVENTORY_INDEX_END = 85;

    public int currentListIndex = 0;
    public final TransientCraftingContainer input = new TransientCraftingContainer(this, 3, 3);
    public final ResultContainer result = new ResultContainer();
    public final CraftingTableIIInventory inventory;
    public CraftingTableIIRecipeManager recipeManager;
    private final Player player;
    private ItemStack lastCraftedItem = ItemStack.EMPTY;
    private int cachedInvChangeCount = -1;
    private final ContainerLevelAccess access;

    public CraftingTableIIScreenHandler(int containerId, Inventory playerInventory, CraftingTableIIEntity entity, ContainerLevelAccess access) {
        super(CraftingTableII.MENU_TYPE, containerId);
        this.access = access;
        this.inventory = new CraftingTableIIInventory(entity);
        this.player = playerInventory.player;

        addSlot(new ResultSlot(player, input, result, 0, -999, -999));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(input, col + row * 3, -999, -999));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 125 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 184));
        }

        for (int row = 0; row < CraftingTableIIInventory.ROWS; row++) {
            for (int col = 0; col < CraftingTableIIInventory.COLS; col++) {
                addSlot(new CraftingTableIISlot(
                        inventory,
                        col + row * CraftingTableIIInventory.COLS,
                        8 + col * 18,
                        18 + row * 18
                ));
            }
        }

        if (player.level().isClientSide) {
            recipeManager = new CraftingTableIIRecipeManager(this, Minecraft.getInstance().player);
        }
    }

    public void tick() {
        if (cachedInvChangeCount != player.getInventory().getTimesChanged()) {
            cachedInvChangeCount = player.getInventory().getTimesChanged();
            updateRecipes(true);
        }
    }

    private void addRecipeItem(ItemStack stack, RecipeHolder<?> recipe) {
        if (recipe == null) {
            return;
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, stack, recipe);
                return;
            }
        }
    }

    private boolean isValidCTIISlot(int index) {
        return index >= 0 && index < slots.size();
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        super.clicked(slotIndex, button, clickType, player);

        if (!player.level().isClientSide || !isValidCTIISlot(slotIndex) || button != 0) {
            return;
        }

        if (!(slots.get(slotIndex) instanceof CraftingTableIISlot slot) || !slot.hasItem()) {
            return;
        }

        RecipeHolder<?> recipe = slot.getRecipe();
        if (recipe == null) {
            return;
        }

        boolean quickCraft = clickType == ClickType.QUICK_MOVE;
        Services.NETWORK.sendCraftPacket(recipe, containerId, quickCraft);
        lastCraftedItem = slot.getItem();
    }

    private Optional<CraftingTableIIRecipeManager.RecipeResult> validateLastCrafted(List<net.minecraft.client.gui.screens.recipebook.RecipeCollection> results) {
        for (var result : results) {
            var pair = CraftingTableIIRecipeManager.firstResult(result);
            if (ItemStack.isSameItem(pair.stack(), lastCraftedItem)) {
                return Optional.of(pair);
            }
        }
        return Optional.empty();
    }

    public void updateRecipes(boolean shouldRefreshInputs) {
        inventory.clearContent();
        if (shouldRefreshInputs && recipeManager != null) {
            recipeManager.refreshInputs();
        }

        List<CraftingTableIIRecipeManager.RecipeResult> newList = new ArrayList<>();

        if (recipeManager != null) {
            validateLastCrafted(recipeManager.results).ifPresentOrElse(
                    newList::add,
                    () -> lastCraftedItem = ItemStack.EMPTY
            );

            int max = CraftingTableIIInventory.SIZE - newList.size();
            for (int i = currentListIndex; i < currentListIndex + max; i++) {
                if (i < recipeManager.results.size()) {
                    newList.add(CraftingTableIIRecipeManager.firstResult(recipeManager.results.get(i)));
                } else {
                    break;
                }
            }
        }

        for (var entry : newList) {
            addRecipeItem(entry.stack(), entry.recipe());
        }
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return index != getResultSlotIndex();
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(access, player, CraftingTableII.BLOCK);
    }

    @Override
    public void fillCraftSlotsStackedContents(net.minecraft.world.entity.player.StackedContents finder) {
        input.fillStackedContents(finder);
    }

    @Override
    public void clearCraftingContent() {
        input.clearContent();
        result.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipe) {
        return recipe.value().matches(input.asCraftInput(), player.level());
    }

    public void updateResultSlot(ItemStack itemStack) {
        result.setItem(0, itemStack);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return input.getWidth();
    }

    @Override
    public int getGridHeight() {
        return input.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        return ItemStack.EMPTY;
    }
}
