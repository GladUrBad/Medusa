package com.gladurbad.antimovehack.listeners.bukkitlisteners;

import com.gladurbad.antimovehack.listeners.nettylisteners.PacketListener;
import com.gladurbad.antimovehack.managers.PlayerDataManager;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class RegistrationListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        PlayerDataManager.getPlayerData().put(playerUUID, new PlayerData(playerUUID, player));
        PacketListener.create(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerDataManager.getPlayerData().remove(playerUUID);
        PacketListener.removePlayer(event.getPlayer());
    }
}
