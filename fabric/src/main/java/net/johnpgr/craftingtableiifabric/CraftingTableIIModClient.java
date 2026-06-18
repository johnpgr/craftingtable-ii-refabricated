package net.johnpgr.craftingtableiifabric;

import net.fabricmc.api.ClientModInitializer;

public class CraftingTableIIModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CraftingTableII.initClient();
    }
}
