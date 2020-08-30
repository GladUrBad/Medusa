package com.gladurbad.medusa.listener;

import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.annotations.PacketHandler;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.impl.PlayerInjectEvent;
import io.github.retrooper.packetevents.event.impl.PlayerUninjectEvent;

public class RegistrationListener implements PacketListener {

    @PacketHandler
    public void onInject(PlayerInjectEvent event) {
        PlayerDataManager.getInstance().getPlayerData().put(event.getPlayer().getUniqueId(), new PlayerData(event.getPlayer().getUniqueId(), event.getPlayer()));
    }

    @PacketHandler
    public void onUninject(PlayerUninjectEvent event) {
        PlayerDataManager.getInstance().getPlayerData().remove(event.getPlayer().getUniqueId());
    }

}
