package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.enums.ServerVersion;
import io.github.retrooper.packetevents.packettype.PacketType;


@CheckInfo(name = "Timer", type = "B")
public class TimerB extends Check {

    private long lastFlyingTime, total;
    private int packets;
    private static final ConfigValue maxPacketDiscrepancy = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-packet-discrepancy");
    private static final ConfigValue maxBuffer = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-buffer");

    public TimerB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && PlayerUtil.getServerVersion().isLowerThan(ServerVersion.v_1_9)) {
            final long flyingTime = now();
            final long flyingTimeDelta = flyingTime - lastFlyingTime;

            total += flyingTimeDelta;
            packets++;

            if (total > 1000L) {
                final int packetDiscrepancy = Math.abs(packets - 20);

                if (packetDiscrepancy >= maxPacketDiscrepancy.getInt()) {
                    if (increaseBuffer() > maxBuffer.getInt()) {
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
