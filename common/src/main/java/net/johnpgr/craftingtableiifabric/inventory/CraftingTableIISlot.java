package net.johnpgr.craftingtableiifabric.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.inventory.Slot;

public class CraftingTableIISlot extends Slot {
    private final int containerSlot;

    public CraftingTableIISlot(CraftingTableIIInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
        containerSlot = slotIndex;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    public Recipe<?> getRecipe() {
        return ((CraftingTableIIInventory) container).getRecipe(containerSlot);
    }
}
