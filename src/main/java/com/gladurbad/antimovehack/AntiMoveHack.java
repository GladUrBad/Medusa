package com.gladurbad.antimovehack;

import com.gladurbad.antimovehack.listeners.bukkitlisteners.RegistrationListener;
import com.gladurbad.antimovehack.listeners.cheatlisteners.*;
import com.gladurbad.antimovehack.listeners.nettylisteners.PacketListener;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class AntiMoveHack extends JavaPlugin {

    @Getter
    private static HashMap<UUID, PlayerData> PLAYER_DATAS = new HashMap<>();
    @Getter
    private static AntiMoveHack antiMoveHack;

    @Override
    public void onEnable() {
        antiMoveHack = this;
        Bukkit.getLogger().info("AntiMoveHack by GladUrBad has been enabled.");

        //Register listeners.
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        //Bukkit listeners.
        pluginManager.registerEvents(new RegistrationListener(), this);
        //Cheat listeners.
        pluginManager.registerEvents(new FlightListener(), this);
        pluginManager.registerEvents(new SpeedListener(), this);
        pluginManager.registerEvents(new FastClimbListener(), this);
        pluginManager.registerEvents(new TimerListener(), this);
        pluginManager.registerEvents(new MotionListener(), this);
        pluginManager.registerEvents(new JesusListener(), this);
        //Netty listeners.
        pluginManager.registerEvents(new PacketListener(), this);

        //Register online players into the system.
        for(final Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if(!PLAYER_DATAS.containsKey(playerUUID)) {
                PLAYER_DATAS.put(playerUUID, new PlayerData(player.getUniqueId(), player));
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
        PLAYER_DATAS.clear();
        PacketListener.INJECTED_PLAYERS.clear();
    }

    public static void addData(UUID playerUUID, PlayerData playerData) {
        PLAYER_DATAS.put(playerUUID, playerData);
    }

    public static PlayerData getPlayerData(Player player) {
        for(PlayerData data : PLAYER_DATAS.values()) {
            if(data.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return data;
            }
        }
        return null;
    }

}
