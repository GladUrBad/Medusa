package com.gladurbad.medusa.listener;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.manager.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

public final class ClientBrandListener implements PluginMessageListener, Listener {

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] msg) {
        try {
            final PlayerData data = Medusa.INSTANCE.getPlayerDataManager().getPlayerData(player);
            if (data == null) return;
            data.setClientBrand(new String(msg, "UTF-8").substring(1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        addChannel(player, "MC|BRAND");
    }

    private void addChannel(final Player player, final String channel) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, channel);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
    }
}
