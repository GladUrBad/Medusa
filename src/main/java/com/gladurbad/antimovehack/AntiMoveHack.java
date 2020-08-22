package com.gladurbad.antimovehack;

import com.gladurbad.antimovehack.listener.bukkitlisteners.RegistrationListener;
import com.gladurbad.antimovehack.listener.nettylisteners.PacketListener;
import com.gladurbad.antimovehack.manager.CheckManager;
import com.gladurbad.antimovehack.manager.PlayerDataManager;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AntiMoveHack extends JavaPlugin {

    @Getter
    private static AntiMoveHack antiMoveHack;

    @Override
    public void onEnable() {
        CheckManager.registerChecks();
        antiMoveHack = this;
        Bukkit.getLogger().info("AntiMoveHack by GladUrBad has been enabled.");

        //Register listeners.
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new RegistrationListener(), this);
        pluginManager.registerEvents(new PacketListener(), this);

        //Register online players into the system.
        for(final Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if(!PlayerDataManager.getPlayerData().containsKey(playerUUID)) {
                PlayerDataManager.getPlayerData().put(playerUUID, new PlayerData(playerUUID, player));
            }
        }

        //Register online players into packet listener'
        for(final Player player : Bukkit.getOnlinePlayers()) {
            final UUID playerUUID = player.getUniqueId();
            if(!PacketListener.INJECTED_PLAYERS.contains(playerUUID)) {
                PacketListener.INJECTED_PLAYERS.add(playerUUID);
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling AntiMoveHack by GladUrBad");
        PlayerDataManager.getPlayerData().clear();
        PacketListener.INJECTED_PLAYERS.clear();
    }


}
