package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.type.EvictingList;

/**
 * Created on 11/13/2020 Package com.gladurbad.medusa.check.impl.player.timer by GladUrBad
 */

@CheckInfo(name = "Timer (A)", description = "Checks for game speed which is too fast.")
public class TimerA extends Check {


    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlyingTime;

    public TimerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TPS)) {
            final long now = now();
            final long delta = now - lastFlyingTime;

            if (delta > 1) {
                samples.add(delta);
            }

            if (samples.isFull()) {
                final double average = samples.stream().mapToDouble(value -> value).average().orElse(1);
                final double speed = 50 / average;

                if (speed >= 1.02) {
                    if (++buffer > 30) {
                        buffer = Math.min(buffer, 50);
                        fail(String.format("speed=%.4f, delta=%o, buffer=%.2f", speed, delta, buffer));
                    }
                } else {
                    buffer = Math.max(0, buffer - 1);
                }
            }

            lastFlyingTime = now;
        } else if (packet.isTeleport()) {
            samples.add(135L); //Magic value. 100L doesn't completely fix it for some reason.
        }
    }
}
