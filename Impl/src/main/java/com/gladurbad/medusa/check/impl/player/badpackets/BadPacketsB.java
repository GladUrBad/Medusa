package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "BadPackets", type = "B", dev = true)
public class BadPacketsB extends Check {

    private int ticksSinceSetting, lastTicksSinceSetting, ticksSinceReset, lastTicksSinceReset;

    public BadPacketsB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && packet.getPacketId() == PacketType.Client.SETTINGS) {
            ticksSinceSetting = 0;
        } else if (packet.isFlying()) {
            ++ticksSinceSetting;
            ++ticksSinceReset;

            if (ticksSinceSetting < 2) {
                increaseBuffer();
                if (buffer > 2) fail();
            } else {
                decreaseBuffer();
            }

            if (ticksSinceSetting < lastTicksSinceSetting) {
                if (lastTicksSinceReset == ticksSinceReset) {
                    increaseBuffer();
                    if (buffer > 1) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }

                lastTicksSinceReset = ticksSinceReset;
                ticksSinceReset = 0;
            }

            lastTicksSinceSetting = ticksSinceSetting;
            }
    }
}