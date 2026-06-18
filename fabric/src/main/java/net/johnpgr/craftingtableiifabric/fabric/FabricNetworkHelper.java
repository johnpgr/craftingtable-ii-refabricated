package net.johnpgr.craftingtableiifabric.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPayload;
import net.johnpgr.craftingtableiifabric.platform.services.INetworkHelper;
import net.minecraft.world.item.crafting.Recipe;

public class FabricNetworkHelper implements INetworkHelper {
    @Override
    public void registerPayloads() {
    }

    @Override
    public void registerServerReceiver() {
        registerPayloads();
        ServerPlayNetworking.registerGlobalReceiver(CraftingTableIIPayload.ID, (server, player, handler, buf, responseSender) -> {
            var payload = CraftingTableIIPayload.read(buf);
            server.execute(() -> CraftingTableIIPayload.handleCraft(payload, player));
        }
        );
    }

    @Override
    public void sendCraftPacket(Recipe<?> recipe, int syncId, boolean quickCraft) {
        var buf = PacketByteBufs.create();
        CraftingTableIIPayload.fromRecipe(recipe, syncId, quickCraft).write(buf);
        ClientPlayNetworking.send(CraftingTableIIPayload.ID, buf);
    }
}
