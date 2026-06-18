package net.johnpgr.craftingtableiifabric.platform.services;

import net.minecraft.world.item.crafting.RecipeHolder;

public interface INetworkHelper {
    void registerPayloads();

    void registerServerReceiver();

    void sendCraftPacket(RecipeHolder<?> recipe, int syncId, boolean quickCraft);
}
