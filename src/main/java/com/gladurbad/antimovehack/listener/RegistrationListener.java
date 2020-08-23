package com.gladurbad.antimovehack.listener;

import com.gladurbad.antimovehack.manager.PlayerDataManager;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import io.github.retrooper.packetevents.annotations.PacketHandler;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.impl.PlayerInjectEvent;
import io.github.retrooper.packetevents.event.impl.PlayerUninjectEvent;

public class RegistrationListener implements PacketListener {

    @PacketHandler
    public void onInject(PlayerInjectEvent event) {
        PlayerDataManager.getPlayerData().put(event.getPlayer().getUniqueId(), new PlayerData(event.getPlayer().getUniqueId(), event.getPlayer()));
    }

    @PacketHandler
    public void onUninject(PlayerUninjectEvent event) {
        PlayerDataManager.getPlayerData().remove(event.getPlayer().getUniqueId());
    }

}
