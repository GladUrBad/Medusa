package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packettype.PacketType;

import java.util.ArrayDeque;

@CheckInfo(name = "Timer", type = "A")
public class TimerA extends Check {

    private long lastTime;
    private long lastDelay;

    private final ArrayDeque<Long> samples = new ArrayDeque<>();

    private static final ConfigValue maxTimerSpeed = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-timer-speed");
    private static final ConfigValue minTimerSpeed = new ConfigValue(ConfigValue.ValueType.DOUBLE, "min-timer-speed");
    private static final ConfigValue customBuffer = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-buffer");
    private static final ConfigValue sampleSize = new ConfigValue(ConfigValue.ValueType.INTEGER, "sample-size");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");
    private static final ConfigValue resetBufferOnFlag = new ConfigValue(ConfigValue.ValueType.BOOLEAN, "reset-buffer-on-flag");


    public TimerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final long time = now();
            final long delay = time - lastTime;

            if (now() - data.getLastSetbackTime() < 1000L ||
                    Math.abs(delay - lastDelay) > 75) samples.clear();

            samples.add(delay);
            if (samples.size() >= sampleSize.getInt()) {
                double timerAverage = samples.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);
                double timerSpeed = 50 / timerAverage;

                if (timerSpeed > maxTimerSpeed.getDouble() || timerSpeed < minTimerSpeed.getDouble()) {
                    if (increaseBuffer() > customBuffer.getDouble()) {
                        fail();
                        if (resetBufferOnFlag.getBoolean()) {
                            setBuffer(0);
                        }
                    }
                } else {
                    decreaseBufferBy(bufferDecay.getDouble());
                }

                samples.clear();
            }
            lastTime = time;
            lastDelay = delay;
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            samples.clear();
        } else if (packet.isReceiving() && packet.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            samples.clear();
        }
    }
}