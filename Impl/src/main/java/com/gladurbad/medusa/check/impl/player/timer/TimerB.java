package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;
import io.github.retrooper.packetevents.packettype.PacketType;

import java.util.ArrayDeque;

@CheckInfo(name = "Timer", type = "B")
public class TimerB extends Check {

    private long lastFlyingTime, total, lastFlyingTimeDelta;
    private int packets;

    private static final ConfigValue maxPacketDiscrepancy = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-packet-discrepancy");
    private static final ConfigValue maxBuffer = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-buffer");

    public TimerB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final long flyingTime = now();
            final long flyingTimeDelta = flyingTime - lastFlyingTime;

            total += flyingTimeDelta;
            packets++;

            if (Math.abs(flyingTimeDelta - lastFlyingTimeDelta) > 175) {
                total = 0;
                packets = 0;
            }

            if (total > 1000L) {
                final int packetDiscrepancy = Math.abs(packets - 20);

                if (packetDiscrepancy >= maxPacketDiscrepancy.getInt()) {
                    if (increaseBuffer() > maxBuffer.getDouble()) {
                        fail();
                        setBuffer(0);
                    }
                } else {
                    decreaseBuffer();
                }

                packets = 0;
                total = 0;
            }

            lastFlyingTime = flyingTime;
            lastFlyingTimeDelta = flyingTimeDelta;
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            packets--;
        }
    }

}
