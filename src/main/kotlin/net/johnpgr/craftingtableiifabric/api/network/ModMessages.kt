package net.johnpgr.craftingtableiifabric.api.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.api.network.packet.CraftingTableIIC2SPacket
import net.minecraft.util.Identifier

object ModMessages {
    private val messageRegistry = mutableSetOf<MessageInfo>()

    fun register(
        identifier: Identifier,
        type: MessageType,
        handler: Any
    ): Identifier {
        messageRegistry.add(MessageInfo(identifier, type, handler))
        return identifier
    }

    fun registerC2S(
        identifier: Identifier,
        handler: ServerPlayNetworking.PlayChannelHandler
    ) {
        ServerPlayNetworking.registerGlobalReceiver(identifier, handler)
    }

    fun registerS2C(
        identifier: Identifier,
        handler: ClientPlayNetworking.PlayChannelHandler
    ) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, handler)
    }

    fun initPackets() {
        messageRegistry.forEach {
            if (it.type == MessageType.C2S) {
                if (it.handler is ServerPlayNetworking.PlayChannelHandler) {
                    registerC2S(it.identifier, it.handler)
                } else {
                    CraftingTableIIFabric.LOGGER.error("Handler for packet ${it.identifier} is not a valid handler")
                }
            }
        }
    }

    fun initPacketsClient() {
        messageRegistry.forEach {
            if (it.type == MessageType.S2C) {
                if (it.handler is ClientPlayNetworking.PlayChannelHandler) {
                    registerS2C(it.identifier, it.handler)
                } else {
                    CraftingTableIIFabric.LOGGER.error("Handler for packet ${it.identifier} is not a valid handler")
                }
            }
        }
    }

    val CTII_CLIENT_SEND_PLAYER_INFO = register(
        CraftingTableIIFabric.id("craftingtableii_client_send_player_info"),
        MessageType.C2S,
        CraftingTableIIC2SPacket
    )
//    val CTII_SERVER_RECEIVE_PLAYER_INFO = register(
//        CraftingTableIIFabric.id("craftingtableii_server_receive_player_info"),
//        MessageType.C2S
//    )
}