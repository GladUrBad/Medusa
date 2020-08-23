package com.gladurbad.antimovehack;

import com.gladurbad.antimovehack.command.AntiMoveHackCommands;
import com.gladurbad.antimovehack.listener.PacketListener;
import com.gladurbad.antimovehack.listener.RegistrationListener;
import com.gladurbad.antimovehack.manager.CheckManager;
import com.gladurbad.antimovehack.manager.PlayerDataManager;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AntiMoveHack extends JavaPlugin {

    @Getter
    private static AntiMoveHack antiMoveHack;
    private final AntiMoveHackCommands antiMoveHackCommands = new AntiMoveHackCommands(this);

    @Override
    public void onLoad() {
        PacketEvents.load();
    }

    @Override
    public void onEnable() {
        CheckManager.registerChecks();
        antiMoveHack = this;
        antiMoveHack.setEnabled(true);
        getCommand("antimovehack").setExecutor(antiMoveHackCommands);

        //Register listeners.
        PacketEvents.getSettings().setIdentifier("antimovehack_handler");
        PacketEvents.start(this);
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());
        PacketEvents.getAPI().getEventManager().registerListener(new RegistrationListener());

        //Register online players into the system.
        for(final Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if(!PlayerDataManager.getPlayerData().containsKey(playerUUID)) {
                PlayerDataManager.getPlayerData().put(playerUUID, new PlayerData(playerUUID, player));
            }
        }

        Bukkit.getLogger().info("AntiMoveHack by GladUrBad has been enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling AntiMoveHack by GladUrBad");
        PacketEvents.stop();
    }


}
