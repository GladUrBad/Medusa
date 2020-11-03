package com.gladurbad.medusa.network;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.annotations.PacketHandler;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;

public class PacketProcessor implements PacketListener {

    @PacketHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        if (Medusa.getInstance().getDataManager().containsPlayer(event.getPlayer())) {
            final PlayerData playerData = Medusa.getInstance().getDataManager().getPlayerData(event.getPlayer());
            playerData.processPacket(new Packet(Packet.Direction.RECEIVE, event.getNMSPacket(), event.getPacketId()));
        }
    }

    @PacketHandler
    public void onPacketSend(PacketSendEvent event) {
        if (Medusa.getInstance().getDataManager().containsPlayer(event.getPlayer())) {
            final PlayerData playerData = Medusa.getInstance().getDataManager().getPlayerData(event.getPlayer());
            playerData.processPacket(new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId()));
        }
    }
}
