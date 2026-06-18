package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPayload;
import net.johnpgr.craftingtableiifabric.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NeoForgeNetworkHelper implements INetworkHelper {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(CraftingTableIIPayload.ID)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();
    private static boolean registered = false;

    @Override
    public void registerPayloads() {
        if (registered) {
            return;
        }
        registered = true;
        CHANNEL.messageBuilder(CraftingTableIIPayload.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CraftingTableIIPayload::write)
                .decoder(CraftingTableIIPayload::read)
                .consumerMainThread((payload, contextSupplier) -> {
                    var context = contextSupplier.get();
                    ServerPlayer player = context.getSender();
                    if (player != null) {
                        CraftingTableIIPayload.handleCraft(payload, player);
                    }
                    context.setPacketHandled(true);
                })
                .add();
    }

    @Override
    public void registerServerReceiver() {
        registerPayloads();
    }

    @Override
    public void sendCraftPacket(Recipe<?> recipe, int syncId, boolean quickCraft) {
        CHANNEL.sendToServer(CraftingTableIIPayload.fromRecipe(recipe, syncId, quickCraft));
    }
}
