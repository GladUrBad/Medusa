package com.gladurbad.medusa;

import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class MedusaPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        PacketEvents.load();
    }

    @Override
    public void onEnable() {
        Medusa.INSTANCE.start(this);
        PacketEvents.init(this);
    }

    @Override
    public void onDisable() {
        PacketEvents.stop();
        Medusa.INSTANCE.stop(this);
    }
}
