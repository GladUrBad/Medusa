package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "BadPackets", type = "E", dev = true)
public class BadPacketsE extends Check {

    private int ticks;
    public BadPacketsE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && (packet.getPacketId() == PacketType.Client.BLOCK_DIG
                || packet.getPacketId() == PacketType.Client.BLOCK_PLACE)) {
            ++ticks;
        } else if (packet.isFlying()) {
            if (ticks >= 2) {
               if (increaseBuffer() > 2) {
                   fail();
               }
            } else {
                resetBuffer();
            }
            ticks = 0;
        }
    }
}
