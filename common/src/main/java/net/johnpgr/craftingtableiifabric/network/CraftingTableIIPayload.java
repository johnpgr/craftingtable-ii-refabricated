package net.johnpgr.craftingtableiifabric.network;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if <1.21.3 {
import net.minecraft.resources.ResourceLocation;
//? }
import net.minecraft.server.level.ServerLevel;
//? if >=1.21.3 {
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
//? }
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

//? if <1.21.3 {
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
//? } else {
public record CraftingTableIIPayload(RecipeDisplayId recipe, int syncId,
                                     boolean quickCraft) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingTableIIPayload> STREAM_CODEC = StreamCodec.composite(
            RecipeDisplayId.STREAM_CODEC,
            CraftingTableIIPayload::recipe,
            ByteBufCodecs.VAR_INT,
            CraftingTableIIPayload::syncId,
            ByteBufCodecs.BOOL,
            CraftingTableIIPayload::quickCraft,
            CraftingTableIIPayload::new
    );
//? }

    public static final CustomPacketPayload.Type<CraftingTableIIPayload> TYPE = new CustomPacketPayload.Type<>(CraftingTableII.id("craft_packet"));

    //? if <1.21.3 {
    public static CraftingTableIIPayload fromRecipe(RecipeHolder<?> recipe, int syncId, boolean quickCraft) {
        return new CraftingTableIIPayload(recipe.id(), syncId, quickCraft);
    }
    //? } else {
    public static CraftingTableIIPayload fromRecipe(RecipeDisplayId recipe, int syncId, boolean quickCraft) {
        return new CraftingTableIIPayload(recipe, syncId, quickCraft);
    }
    //? }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleCraft(CraftingTableIIPayload data, ServerPlayer player) {
        if (player.containerMenu.containerId != data.syncId() || !(player.containerMenu instanceof CraftingTableIIScreenHandler craftingScreenHandler)) {
            return;
        }

        //? if <1.21.3 {
        Optional<RecipeHolder<?>> optionalRecipe = player.getServer().getRecipeManager().byKey(data.recipe());
        if (optionalRecipe.isEmpty() || !(optionalRecipe.get().value() instanceof CraftingRecipe craftingRecipe)) {
            return;
        }

        RecipeHolder<?> recipeHolder = optionalRecipe.get();
        //? } else {
        var displayInfo = player.getServer().getRecipeManager().getRecipeFromDisplay(data.recipe());
        if (displayInfo == null || !(displayInfo.parent().value() instanceof CraftingRecipe craftingRecipe)) {
            return;
        }

        RecipeHolder<?> recipeHolder = displayInfo.parent();
        //? }

        //? if <1.21.3 {
        craftingScreenHandler.handlePlacement(data.quickCraft(), recipeHolder, player);
        //? } else {
        craftingScreenHandler.handlePlacement(data.quickCraft(), false, recipeHolder, (ServerLevel) player.level(), player.getInventory());
        //? }

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
