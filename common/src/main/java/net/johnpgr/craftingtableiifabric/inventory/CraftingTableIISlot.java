package net.johnpgr.craftingtableiifabric.inventory;

import net.minecraft.world.item.ItemStack;
//? if <1.21.3 {
import net.minecraft.world.item.crafting.RecipeHolder;
//? } else {
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
//? }
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

    //? if <1.21.3 {
    public RecipeHolder<?> getRecipe() {
    //? } else {
    public RecipeDisplayId getRecipe() {
    //? }
        return ((CraftingTableIIInventory) container).getRecipe(containerSlot);
    }
}
