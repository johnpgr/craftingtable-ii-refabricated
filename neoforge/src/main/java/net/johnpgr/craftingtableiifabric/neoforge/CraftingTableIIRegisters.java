package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.core.BlockPos;
//? if >=1.21.3 {
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
//? }
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public final class CraftingTableIIRegisters {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CraftingTableII.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CraftingTableII.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CraftingTableII.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CraftingTableII.MOD_ID);

    public static final DeferredBlock<Block> CRAFTING_TABLE_BLOCK =
            BLOCKS.register("crafting_table_ii", () -> CraftingTableII.BLOCK);
    public static final DeferredItem<Item> CRAFTING_TABLE_ITEM =
            ITEMS.register("crafting_table_ii", () -> {
                var properties = new Item.Properties();
                //? if >=1.21.3 {
                properties
                        .setId(ResourceKey.create(Registries.ITEM, CraftingTableII.id("crafting_table_ii")))
                        .useBlockDescriptionPrefix();
                //? }
                return new BlockItem(CraftingTableII.BLOCK, properties);
            });
	    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CraftingTableIIEntity>> CRAFTING_TABLE_ENTITY =
	            BLOCK_ENTITY_TYPES.register("crafting_table_ii", () -> {
	                //? if >=1.21.3 {
	                CraftingTableII.ENTITY_TYPE = new BlockEntityType<>(
	                        CraftingTableIIEntity::new,
	                        CraftingTableII.BLOCK
	                );
	                //? } else {
	                /*CraftingTableII.ENTITY_TYPE = BlockEntityType.Builder.of(
	                       CraftingTableIIEntity::new,
	                       CraftingTableII.BLOCK
	                ).build(null);*/
	                //? }
	                return CraftingTableII.ENTITY_TYPE;
	            });
    public static final DeferredHolder<MenuType<?>, MenuType<CraftingTableIIScreenHandler>> CRAFTING_TABLE_MENU =
            MENUS.register("crafting_table_ii", () -> {
                CraftingTableII.MENU_TYPE = IMenuTypeExtension.create((windowId, inv, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    var level = inv.player.level();
                    var entity = (CraftingTableIIEntity) level.getBlockEntity(pos);
                    return new CraftingTableIIScreenHandler(
                            windowId,
                            inv,
                            entity,
                            ContainerLevelAccess.create(level, pos)
                    );
                });
                return CraftingTableII.MENU_TYPE;
            });

    private CraftingTableIIRegisters() {}

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        MENUS.register(modBus);
    }
}
