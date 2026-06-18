package net.johnpgr.craftingtableiifabric.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;

public interface IPlatformHelper {
    void bootstrap();

    File getConfigDirectory();

    void openMenu(ServerPlayer player, BlockPos pos);
}
