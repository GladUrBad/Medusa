package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.abilities.WrappedPacketInAbilities;
import io.github.retrooper.packetevents.packetwrappers.out.abilities.WrappedPacketOutAbilities;

@CheckInfo(name = "BadPackets", type = "B", dev = true)
public class BadPacketsB extends Check {

    private boolean server;

    public BadPacketsB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isSending() && packet.getPacketId() == PacketType.Server.ABILITIES) {
            final WrappedPacketOutAbilities abilities = new WrappedPacketOutAbilities(packet.getRawPacket());
            server = abilities.isFlightAllowed();
        } else if (packet.isReceiving() && packet.getPacketId() == PacketType.Client.ABILITIES) {
            final WrappedPacketInAbilities abilities = new WrappedPacketInAbilities(packet.getRawPacket());
            if (abilities.isFlightAllowed() != server) fail();
        }
    }
}