package com.gladurbad.antimovehack.listeners.bukkitlisteners;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.listeners.nettylisteners.PacketListener;
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
        AntiMoveHack.addData(player.getUniqueId(), new PlayerData(playerUUID, player));
        PacketListener.create(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        AntiMoveHack.getPLAYER_DATAS().remove(playerUUID);
        PacketListener.removePlayer(event.getPlayer());
    }
}
