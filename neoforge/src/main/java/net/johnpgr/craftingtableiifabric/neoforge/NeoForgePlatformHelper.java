package net.johnpgr.craftingtableiifabric.neoforge;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity;
import net.johnpgr.craftingtableiifabric.platform.services.IPlatformHelper;
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public void bootstrap() {
        // Deferred registers handle content registration during mod construction.
    }

    @Override
    public File getConfigDirectory() {
        return CraftingTableIINeoForge.getConfigDirectory();
    }

    @Override
    public void openMenu(ServerPlayer player, BlockPos pos) {
        player.openMenu(new MenuProvider() {
            @Override @NotNull
            public Component getDisplayName() {
                return Component.translatable("screen." + CraftingTableII.MOD_ID + ".crafting_table_ii");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                var level = player.level();
                var entity = (CraftingTableIIEntity) level.getBlockEntity(pos);
                return new CraftingTableIIScreenHandler(
                        containerId,
                        playerInventory,
                        entity,
                        ContainerLevelAccess.create(level, pos)
                );
            }
        }, buf -> buf.writeBlockPos(pos));
    }
}
