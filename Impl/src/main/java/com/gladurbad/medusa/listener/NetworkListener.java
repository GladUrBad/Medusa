package com.gladurbad.medusa.listener;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;

import com.gladurbad.medusa.manager.PlayerDataManager;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.annotation.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.event.priority.PacketEventPriority;


public final class NetworkListener implements PacketListener {

    @PacketHandler
    public void onPacketReceive(final PacketReceiveEvent event) {
        final PlayerData data = Medusa.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer());

        if (data != null) {
            Medusa.INSTANCE.getPacketExecutor().execute(() -> Medusa.INSTANCE.getReceivingPacketProcessor()
                    .handle(data, new Packet(Packet.Direction.RECEIVE, event.getNMSPacket(), event.getPacketId())));
        }
    }

    @PacketHandler
    public void onPacketSend(final PacketSendEvent event) {
        final PlayerData data = Medusa.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer());

        if (data != null) {
            Medusa.INSTANCE.getPacketExecutor().execute(() -> Medusa.INSTANCE.getSendingPacketProcessor()
                    .handle(data, new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId())));
        }
    }
}
