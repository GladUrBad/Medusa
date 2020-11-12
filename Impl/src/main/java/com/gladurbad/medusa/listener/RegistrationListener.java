package com.gladurbad.medusa.listener;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.playerdata.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RegistrationListener implements Listener {

    @EventHandler
    public void onInject(PlayerJoinEvent event) {
        final PlayerData playerData = new PlayerData(event.getPlayer().getUniqueId(), event.getPlayer());
        if (Config.TESTMODE) {
            playerData.setAlerts(true);
        }
        Medusa.getInstance().getDataManager().getPlayerData().put(event.getPlayer().getUniqueId(), playerData);
    }

    @EventHandler
    public void onUninject(PlayerQuitEvent event) {
        Medusa.getInstance().getDataManager().getPlayerData().remove(event.getPlayer().getUniqueId());
    }

}
