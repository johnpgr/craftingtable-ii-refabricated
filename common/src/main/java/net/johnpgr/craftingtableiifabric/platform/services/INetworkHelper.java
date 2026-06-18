package net.johnpgr.craftingtableiifabric.platform.services;

import net.minecraft.resources.ResourceLocation;

public interface INetworkHelper {
    void registerPayloads();

    void registerServerReceiver();

    void sendCraftPacket(ResourceLocation recipeId, int syncId, boolean quickCraft);
}
