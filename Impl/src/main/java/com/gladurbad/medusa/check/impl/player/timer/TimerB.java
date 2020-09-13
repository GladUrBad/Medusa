package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Timer", type = "B")
public class TimerB extends Check {

    private long lastFlyingTime, total;
    private int packets;

    public TimerB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && isFlyingPacket(packet)) {
            final long flyingTime = now();
            final long flyingTimeDelta = flyingTime - lastFlyingTime;

            total += flyingTimeDelta;
            packets++;

            if (total > 1000L) {
                final int packetDiscrepancy = Math.abs(packets - 20);

                if (packetDiscrepancy >= 2) {
                    increaseBuffer();
                    if (buffer > 3) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }

                packets = 0;
                total = 0;
            }
            lastFlyingTime = flyingTime;
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            packets--;
        }
    }

}
