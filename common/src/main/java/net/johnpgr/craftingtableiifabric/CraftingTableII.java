package net.johnpgr.craftingtableiifabric;

import net.johnpgr.craftingtableiifabric.block.CraftingTableIIBlock;
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.platform.Services;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CraftingTableII {
    public static final String MOD_ID = "craftingtableiifabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CraftingTableIIBlock BLOCK = new CraftingTableIIBlock();

    public static BlockEntityType<CraftingTableIIEntity> ENTITY_TYPE;
    public static MenuType<CraftingTableIIScreenHandler> MENU_TYPE;

    private CraftingTableII() {
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static void init() {
        Services.PLATFORM.bootstrap();
        Services.NETWORK.registerServerReceiver();
    }

    public static void initClient() {
        Services.CLIENT.bootstrap();
        CraftingTableIIDescriptions.register();
    }
}
