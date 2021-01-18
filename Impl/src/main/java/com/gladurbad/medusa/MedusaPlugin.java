package com.gladurbad.medusa;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.plugin.java.JavaPlugin;

public final class MedusaPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        PacketEvents.create(this).getSettings()
                .injectAsync(true)
                .ejectAsync(true)
                .injectEarly(true)
                .checkForUpdates(true)
                .backupServerVersion(ServerVersion.v_1_8_8);

        PacketEvents.get().load();
    }

    @Override
    public void onEnable() {
        Medusa.INSTANCE.start(this);
        PacketEvents.get().init(this);
    }

    @Override
    public void onDisable() {
        PacketEvents.get().stop();
        Medusa.INSTANCE.stop(this);
    }
}
