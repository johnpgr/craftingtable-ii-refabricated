package net.johnpgr.craftingtableiifabric.forge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPayload;
import net.johnpgr.craftingtableiifabric.platform.services.INetworkHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;

public class ForgeNetworkHelper implements INetworkHelper {
    private static final Channel<CustomPacketPayload> CHANNEL = ChannelBuilder
            .named(CraftingTableII.id("main"))
            .networkProtocolVersion(1)
            .optional()
            .payloadChannel()
            .play()
            .serverbound()
            .addMain(
                    CraftingTableIIPayload.TYPE,
                    CraftingTableIIPayload.STREAM_CODEC,
                    (payload, context) -> {
                        ServerPlayer player = context.getSender();
                        if (player != null) {
                            CraftingTableIIPayload.handleCraft(payload, player);
                        }
                    }
            ).build();

    @Override
    public void registerPayloads() {
        // Registered through static channel initialization.
    }

    @Override
    public void registerServerReceiver() {
        // Registered through static channel initialization.
    }


    @Override
    public void sendCraftPacket(ResourceLocation recipeId, int syncId, boolean quickCraft) {
        CHANNEL.send(
                new CraftingTableIIPayload(recipeId, syncId, quickCraft),
                PacketDistributor.SERVER.noArg()
        );
    }

}