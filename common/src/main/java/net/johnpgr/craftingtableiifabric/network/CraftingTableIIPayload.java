package net.johnpgr.craftingtableiifabric.network;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public record CraftingTableIIPayload(ResourceLocation recipe, int syncId, boolean quickCraft) {
    public static final ResourceLocation ID = CraftingTableII.id("craft_packet");

    public static CraftingTableIIPayload fromRecipe(Recipe<?> recipe, int syncId, boolean quickCraft) {
        return new CraftingTableIIPayload(recipe.getId(), syncId, quickCraft);
    }

    public static CraftingTableIIPayload read(FriendlyByteBuf buf) {
        return new CraftingTableIIPayload(buf.readResourceLocation(), buf.readInt(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(recipe);
        buf.writeInt(syncId);
        buf.writeBoolean(quickCraft);
    }

    @SuppressWarnings("unchecked")
    public static void handleCraft(CraftingTableIIPayload data, ServerPlayer player) {
        if (player.containerMenu.containerId != data.syncId() || !(player.containerMenu instanceof CraftingTableIIScreenHandler craftingScreenHandler)) {
            return;
        }

        Optional<? extends Recipe<?>> optionalRecipe = player.server.getRecipeManager().byKey(data.recipe());
        if (optionalRecipe.isEmpty() || !(optionalRecipe.get() instanceof CraftingRecipe)) {
            return;
        }

        Recipe<net.minecraft.world.inventory.CraftingContainer> recipe = (Recipe<net.minecraft.world.inventory.CraftingContainer>) optionalRecipe.get();

        craftingScreenHandler.handlePlacement(data.quickCraft(), recipe, player);

        while (recipe.matches(craftingScreenHandler.input, player.level())) {
            ItemStack cursor = craftingScreenHandler.getCarried();
            ItemStack output = recipe.assemble(craftingScreenHandler.input, player.server.registryAccess());

            craftingScreenHandler.updateResultSlot(output);

            Slot resultSlot = craftingScreenHandler.getSlot(craftingScreenHandler.getResultSlotIndex());
            resultSlot.onTake(player, output);

            if (cursor.isEmpty()) {
                craftingScreenHandler.setCarried(output);
            } else if (ItemStack.isSameItem(cursor, output) && cursor.isStackable() && cursor.getCount() + output.getCount() <= cursor.getMaxStackSize()) {
                cursor.grow(output.getCount());
            } else if (!player.getInventory().add(output)) {
                player.drop(output, false);
            }
        }

        craftingScreenHandler.clearCraftingContent();
    }
}
