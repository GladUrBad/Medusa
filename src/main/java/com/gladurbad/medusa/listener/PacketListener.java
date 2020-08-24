package com.gladurbad.medusa.listener;

import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.annotations.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;

public class PacketListener implements io.github.retrooper.packetevents.event.PacketListener {

    @PacketHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        if(PlayerDataManager.containsPlayer(event.getPlayer())) {
            final PlayerData playerData = PlayerDataManager.getPlayerData().get(event.getPlayer().getUniqueId());
            playerData.processPacket(new Packet(Packet.Direction.RECEIVE, event.getNMSPacket(), event.getPacketId()));
        }
    }

    @PacketHandler
    public void onPacketSend(PacketSendEvent event) {
        if(PlayerDataManager.containsPlayer(event.getPlayer())) {
            final PlayerData playerData = PlayerDataManager.getPlayerData().get(event.getPlayer().getUniqueId());
            playerData.processPacket(new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId()));
        }
    }
}
