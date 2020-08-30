package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.Config;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;

import java.util.ArrayDeque;

@CheckInfo(name = "Autoclicker", type = "A", dev = false)
public class AutoclickerA extends Check {

    private long lastClickTime;
    private ArrayDeque<Long> samples = new ArrayDeque<>();

    public AutoclickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && packet.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            final long clickTime = now();
            final long clickDelay = clickTime - lastClickTime;
            samples.add(clickDelay);
            if(samples.size() >= 20) {
                double averageCps = 1000D / samples.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);

                if(averageCps > Config.MAX_CPS) fail();
                samples.clear();
            }
            lastClickTime = clickTime;
        }
    }
}
