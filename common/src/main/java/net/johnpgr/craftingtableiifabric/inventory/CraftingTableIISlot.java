package net.johnpgr.craftingtableiifabric.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.inventory.Slot;

public class CraftingTableIISlot extends Slot {
    private final int containerSlot;

    public CraftingTableIISlot(CraftingTableIIInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.containerSlot = index;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    public RecipeHolder<?> getRecipe() {
        return ((CraftingTableIIInventory) container).getRecipe(containerSlot);
    }
}
