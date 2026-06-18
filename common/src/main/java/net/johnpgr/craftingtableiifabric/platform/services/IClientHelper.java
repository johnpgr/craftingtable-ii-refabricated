package net.johnpgr.craftingtableiifabric.platform.services;

public interface IClientHelper {
    void bootstrap();

    void onClientStarted(Runnable callback);
}
