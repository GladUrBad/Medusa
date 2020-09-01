package com.gladurbad.medusa;

import com.gladurbad.medusa.command.MedusaCommands;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.network.PacketProcessor;
import com.gladurbad.medusa.listener.RegistrationListener;
import com.gladurbad.medusa.manager.CheckManager;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Medusa extends JavaPlugin {

    @Getter
    private static Medusa instance;

    @Override
    public void onLoad() {
        PacketEvents.load();
    }

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        Config.updateConfig();

        MedusaCommands medusaCommands = new MedusaCommands(this);

        CheckManager.registerChecks();

        getCommand("medusa").setExecutor(medusaCommands);

        //Register listeners.
        PacketEvents.start(this);
        PacketEvents.getAPI().getEventManager().registerListener(new PacketProcessor());
        PacketEvents.getAPI().getEventManager().registerListener(new RegistrationListener());


        //Register online players into the system.
        for(final Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if(!PlayerDataManager.getInstance().containsPlayer(playerUUID)) {
                PlayerDataManager.getInstance().getPlayerData().put(playerUUID, new PlayerData(playerUUID, player));
            }
        }

        Bukkit.getLogger().info("Medusa by GladUrBad has been enabled.");
        instance.setEnabled(true);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling Medusa by GladUrBad");
        PacketEvents.stop();
    }


}
