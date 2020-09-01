package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;

import java.util.ArrayDeque;

@CheckInfo(name = "Timer", type = "A", dev = true)
public class TimerA extends Check {

    private long lastTime;
    private ArrayDeque<Long> samples = new ArrayDeque<>();

    public TimerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            final long time = System.currentTimeMillis();
            final long delay = time - lastTime;

            if(now() - data.getLastSetbackTime() < 1000L) samples.clear();

            samples.add(delay);
            if (samples.size() >= 20) {
                double timerAverage = samples.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);
                double timerSpeed = 50 / timerAverage;

                if(timerSpeed > 1.05 || timerSpeed < 0.95) fail();

                samples.clear();
            }
            lastTime = time;
        } else if(packet.isSending() && packet.getPacketId() == PacketType.Server.ENTITY_TELEPORT) {
            samples.clear();
        } else if(packet.isSending() && packet.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            samples.clear();
        }
    }
}
