package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.core.BlockPos;
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

public final class CraftingTableIIRegisters {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CraftingTableII.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CraftingTableII.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, CraftingTableII.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, CraftingTableII.MOD_ID);

    public static final DeferredBlock<Block> CRAFTING_TABLE_BLOCK =
            BLOCKS.register("crafting_table_ii", () -> CraftingTableII.BLOCK);
    public static final DeferredItem<Item> CRAFTING_TABLE_ITEM =
            ITEMS.register("crafting_table_ii", () -> new BlockItem(CraftingTableII.BLOCK, new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CraftingTableIIEntity>> CRAFTING_TABLE_ENTITY =
            BLOCK_ENTITY_TYPES.register("crafting_table_ii", () -> {
                CraftingTableII.ENTITY_TYPE = BlockEntityType.Builder.of(
                        CraftingTableIIEntity::new,
                        CraftingTableII.BLOCK
                ).build(null);
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
