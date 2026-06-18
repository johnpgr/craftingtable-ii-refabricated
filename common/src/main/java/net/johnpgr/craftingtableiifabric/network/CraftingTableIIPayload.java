package net.johnpgr.craftingtableiifabric.network;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record CraftingTableIIPayload(ResourceLocation recipe, int syncId,
                                     boolean quickCraft) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingTableIIPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            CraftingTableIIPayload::recipe,
            ByteBufCodecs.VAR_INT,
            CraftingTableIIPayload::syncId,
            ByteBufCodecs.BOOL,
            CraftingTableIIPayload::quickCraft,
            CraftingTableIIPayload::new
    );

    public static final CustomPacketPayload.Type<CraftingTableIIPayload> TYPE = new CustomPacketPayload.Type<>(CraftingTableII.id("craft_packet"));

    public static CraftingTableIIPayload fromRecipe(RecipeHolder<?> recipe, int syncId, boolean quickCraft) {
        return new CraftingTableIIPayload(recipe.id(), syncId, quickCraft);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleCraft(CraftingTableIIPayload data, ServerPlayer player) {
        if (player.containerMenu.containerId != data.syncId() || !(player.containerMenu instanceof CraftingTableIIScreenHandler craftingScreenHandler)) {
            return;
        }

        Optional<RecipeHolder<?>> optionalRecipe = player.server.getRecipeManager().byKey(data.recipe());
        if (optionalRecipe.isEmpty() || !(optionalRecipe.get().value() instanceof CraftingRecipe craftingRecipe)) {
            return;
        }

        RecipeHolder<?> recipeHolder = optionalRecipe.get();

        craftingScreenHandler.handlePlacement(data.quickCraft(), recipeHolder, player);

        while (craftingRecipe.matches(craftingScreenHandler.input.asCraftInput(), player.level())) {
            var craftInput = craftingScreenHandler.input.asCraftInput();
            ItemStack cursor = craftingScreenHandler.getCarried();
            ItemStack output = craftingRecipe.assemble(craftInput, player.registryAccess());

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
