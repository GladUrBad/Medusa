package com.gladurbad.medusa.listener;

import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.annotations.PacketHandler;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.impl.PlayerInjectEvent;
import io.github.retrooper.packetevents.event.impl.PlayerUninjectEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerRiptideEvent;

public class ModernVersionListeners implements Listener {

    @EventHandler
    public void onPlayerRiptide(PlayerRiptideEvent event) {
        final PlayerData playerData = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());
        if(playerData != null){
            playerData.setRiptideTicks(60);
        }
    }
    @EventHandler
    public void onPlayerToggleGlide(EntityToggleGlideEvent event) {
        if(event.getEntity() instanceof Player) {
            final PlayerData playerData = PlayerDataManager.getInstance().getPlayerData((Player) event.getEntity());
            if (playerData != null) {
                playerData.setGliding(event.isGliding());
            }
        }
    }
}
