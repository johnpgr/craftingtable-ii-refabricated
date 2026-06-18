package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPayload;
import net.johnpgr.craftingtableiifabric.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
//? if <1.21.3 {
import net.minecraft.world.item.crafting.RecipeHolder;
//? } else {
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
//? }
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NeoForgeNetworkHelper implements INetworkHelper {
    @Override
    public void registerPayloads() {
        // Registered through NeoForge event bus.
    }

    @Override
    public void registerServerReceiver() {
        // Registered through NeoForge event bus.
    }

    @Override
    //? if <1.21.3 {
    public void sendCraftPacket(RecipeHolder<?> recipe, int syncId, boolean quickCraft) {
    //? } else {
    public void sendCraftPacket(RecipeDisplayId recipe, int syncId, boolean quickCraft) {
    //? }
        PacketDistributor.sendToServer(CraftingTableIIPayload.fromRecipe(recipe, syncId, quickCraft));
    }

    @EventBusSubscriber(modid = CraftingTableII.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class PayloadRegistration {
        @SubscribeEvent
        public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
            event.registrar(CraftingTableII.MOD_ID)
                    .playToServer(
                            CraftingTableIIPayload.TYPE,
                            CraftingTableIIPayload.STREAM_CODEC,
                            (payload, context) -> {
                                if (context.player() instanceof ServerPlayer player) {
                                    CraftingTableIIPayload.handleCraft(payload, player);
                                }
                            }
                    );
        }
    }
}
