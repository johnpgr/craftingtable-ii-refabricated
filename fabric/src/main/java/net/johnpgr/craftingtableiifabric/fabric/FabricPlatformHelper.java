package net.johnpgr.craftingtableiifabric.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.platform.services.IPlatformHelper;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public void bootstrap() {
        CraftingTableII.ENTITY_TYPE = FabricBlockEntityTypeBuilder.create(
                CraftingTableIIEntity::new,
                CraftingTableII.BLOCK
        ).build(null);

        CraftingTableII.MENU_TYPE = new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> {
            BlockPos pos = buf.readBlockPos();
            var player = playerInventory.player;
            var level = player.level();
            var entity = (CraftingTableIIEntity) level.getBlockEntity(pos);
            return new CraftingTableIIScreenHandler(
                    syncId,
                    playerInventory,
                    entity,
                    ContainerLevelAccess.create(level, pos)
            );
        });

        var blockId = CraftingTableII.id("crafting_table_ii");
        Registry.register(
                BuiltInRegistries.BLOCK,
                blockId,
                CraftingTableII.BLOCK
        );
        Registry.register(
                BuiltInRegistries.ITEM,
                blockId,
                new BlockItem(CraftingTableII.BLOCK, new Item.Properties())
        );
        Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                blockId,
                CraftingTableII.ENTITY_TYPE
        );
        Registry.register(
                BuiltInRegistries.MENU,
                blockId,
                CraftingTableII.MENU_TYPE
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> content.addAfter(Items.CRAFTING_TABLE, CraftingTableII.BLOCK));
    }

    @Override
    public File getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    @Override
    public void openMenu(ServerPlayer player, BlockPos pos) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            @NotNull
            public Component getDisplayName() {
                return Component.translatable("screen." + CraftingTableII.MOD_ID + ".crafting_table_ii");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                var level = player.level();
                var entity = (CraftingTableIIEntity) level.getBlockEntity(pos);
                return new CraftingTableIIScreenHandler(
                        syncId,
                        playerInventory,
                        entity,
                        ContainerLevelAccess.create(level, pos)
                );
            }
        });
    }
}
