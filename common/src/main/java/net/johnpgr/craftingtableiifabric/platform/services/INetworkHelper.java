package net.johnpgr.craftingtableiifabric.platform.services;

import net.minecraft.world.item.crafting.Recipe;

public interface INetworkHelper {
    void registerPayloads();

    void registerServerReceiver();

    void sendCraftPacket(Recipe<?> recipe, int syncId, boolean quickCraft);
}
