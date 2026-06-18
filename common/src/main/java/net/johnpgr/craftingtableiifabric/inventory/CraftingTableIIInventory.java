package net.johnpgr.craftingtableiifabric.inventory;

import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CraftingTableIIInventory implements Container {
    public static final int COLS = 8;
    public static final int ROWS = 5;
    public static final int SIZE = ROWS * COLS;

    private final CraftingTableIIEntity entity;
    private final List<RecipeHolder<?>> recipes = new ArrayList<>(Collections.nCopies(SIZE, null));

    public CraftingTableIIInventory(CraftingTableIIEntity blockEntity) {
        entity = blockEntity;
    }

    @Override
    public int getContainerSize() {
        return entity.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return entity.isEmpty();
    }

    @Override
    @NotNull
    public ItemStack getItem(int slot) {
        return entity.getItem(slot);
    }

    @Override
    @NotNull
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        entity.setItem(slot, stack.copy());
    }

    public void setItem(int slot, ItemStack stack, RecipeHolder<?> recipe) {
        setItem(slot, stack);
        recipes.set(slot, recipe);
    }

    public RecipeHolder<?> getRecipe(int slot) {
        if (slot < 0 || slot >= recipes.size()) {
            return null;
        }
        return recipes.get(slot);
    }

    @Override
    public void setChanged() {
        entity.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return entity.stillValid(player);
    }

    @Override
    public void clearContent() {
        entity.clearContent();
        Collections.fill(recipes, null);
    }
}
