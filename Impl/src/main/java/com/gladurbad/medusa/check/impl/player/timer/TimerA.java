package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.enums.ServerVersion;
import io.github.retrooper.packetevents.packettype.PacketType;

import java.util.ArrayDeque;


@CheckInfo(name = "Timer", type = "A")
public class TimerA extends Check {

    private long lastTime;
    private final ArrayDeque<Long> samples = new ArrayDeque<>();

    private static final ConfigValue maxTimerSpeed = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-timer-speed");
    private static final ConfigValue minTimerSpeed = new ConfigValue(ConfigValue.ValueType.DOUBLE, "min-timer-speed");
    private static final ConfigValue customBuffer = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-buffer");

    public TimerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final long time = now();
            final long delay = time - lastTime;

            if (now() - data.getLastSetbackTime() < 1000L) samples.clear();

            samples.add(delay);
            if (samples.size() >= 20) {
                double timerAverage = samples.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);
                double timerSpeed = 50 / timerAverage;

                if (timerSpeed > maxTimerSpeed.getDouble() || (timerSpeed < minTimerSpeed.getDouble() && PlayerUtil.getServerVersion().isLowerThan(ServerVersion.v_1_9))) {
                   if (increaseBuffer() > customBuffer.getInt()) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }
                samples.clear();
            }
            lastTime = time;
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.ENTITY_TELEPORT) {
            samples.clear();
        } else if (packet.isReceiving() && packet.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            samples.clear();
        }
    }
}