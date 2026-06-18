package net.johnpgr.craftingtableiifabric.platform.services;

//? if <1.21.3 {
import net.minecraft.world.item.crafting.RecipeHolder;
//? } else {
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
//? }

public interface INetworkHelper {
    void registerPayloads();

    void registerServerReceiver();

    //? if <1.21.3 {
    void sendCraftPacket(RecipeHolder<?> recipe, int syncId, boolean quickCraft);
    //? } else {
    void sendCraftPacket(RecipeDisplayId recipe, int syncId, boolean quickCraft);
    //? }
}
