package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.renderer.CraftingTableIIItemRenderer;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class CraftingTableIIRegisters {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CraftingTableII.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CraftingTableII.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CraftingTableII.MOD_ID);

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CraftingTableII.MOD_ID);

    public static final RegistryObject<Block> CRAFTING_TABLE_BLOCK =
            BLOCKS.register("crafting_table_ii", () -> CraftingTableII.BLOCK);

    public static final RegistryObject<Item> CRAFTING_TABLE_ITEM =
            ITEMS.register("crafting_table_ii", () -> {
                var properties = new Item.Properties();
                return new BlockItem(CraftingTableII.BLOCK, properties) {
                    @Override
                    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                        consumer.accept(new IClientItemExtensions() {
                            @Override @NotNull
                            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                                Minecraft client = Minecraft.getInstance();
                                return new CraftingTableIIItemRenderer(
                                        client.getBlockEntityRenderDispatcher(),
                                        client.getEntityModels()
                                );
                            }
                        });
                    }
                };
            });

    public static final RegistryObject<BlockEntityType<CraftingTableIIEntity>> CRAFTING_TABLE_ENTITY =
            BLOCK_ENTITY_TYPES.register("crafting_table_ii", () -> {
                CraftingTableII.ENTITY_TYPE = BlockEntityType.Builder.of(
                        CraftingTableIIEntity::new,
                        CraftingTableII.BLOCK
                ).build(null);
                return CraftingTableII.ENTITY_TYPE;
            });

    public static final RegistryObject<MenuType<CraftingTableIIScreenHandler>> CRAFTING_TABLE_MENU =
            MENUS.register("crafting_table_ii", () -> {
                CraftingTableII.MENU_TYPE = IForgeMenuType.create((windowId, inv, buf) -> {
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
