package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "BadPackets", type = "D", dev = true)
public class BadPacketsD extends Check {

    private int ticks;
    public BadPacketsD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            final byte id = packet.getPacketId();

            if (id == PacketType.Client.ENTITY_ACTION) {
                if (++ticks > 4) {
                    fail();
                }
            } else if (packet.isFlying()) {
                ticks = 0;
            }
        }
    }
}
