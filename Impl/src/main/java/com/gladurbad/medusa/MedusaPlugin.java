package com.gladurbad.medusa;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.plugin.java.JavaPlugin;

public final class MedusaPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        PacketEvents.create(this).getSettings()
                .compatInjector(true)
                .checkForUpdates(true)
                .backupServerVersion(ServerVersion.v_1_8_8);
        PacketEvents.get().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.get().init(this);
        Medusa.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        PacketEvents.get().terminate();
        Medusa.INSTANCE.stop(this);
    }
}
