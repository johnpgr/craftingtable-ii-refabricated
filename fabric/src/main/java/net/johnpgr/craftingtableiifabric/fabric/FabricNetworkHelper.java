package net.johnpgr.craftingtableiifabric.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPayload;
import net.johnpgr.craftingtableiifabric.platform.services.INetworkHelper;
//? if <1.21.3 {
import net.minecraft.world.item.crafting.RecipeHolder;
//? } else {
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
//? }

public class FabricNetworkHelper implements INetworkHelper {
    @Override
    public void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(CraftingTableIIPayload.TYPE, CraftingTableIIPayload.STREAM_CODEC);
    }

    @Override
    public void registerServerReceiver() {
        registerPayloads();
        ServerPlayNetworking.registerGlobalReceiver(CraftingTableIIPayload.TYPE, (payload, context) -> context.server().execute(() ->
                CraftingTableIIPayload.handleCraft(payload, context.player()))
        );
    }

    @Override
    //? if <1.21.3 {
    public void sendCraftPacket(RecipeHolder<?> recipe, int syncId, boolean quickCraft) {
    //? } else {
    public void sendCraftPacket(RecipeDisplayId recipe, int syncId, boolean quickCraft) {
    //? }
        ClientPlayNetworking.send(CraftingTableIIPayload.fromRecipe(recipe, syncId, quickCraft));
    }
}
