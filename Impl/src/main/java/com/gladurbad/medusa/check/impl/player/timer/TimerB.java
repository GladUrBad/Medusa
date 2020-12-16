package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.type.EvictingList;

/**
 * Created on 11/13/2020 Package com.gladurbad.medusa.check.impl.player.timer by GladUrBad
 */

@CheckInfo(name = "Timer (B)",  description = "Checks for game speed which is too slow.", experimental = true)
public class TimerB extends Check {

    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlyingTime;


    public TimerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = now();
            final long delta = now - lastFlyingTime;

            samples.add(delta);
            if (samples.isFull()) {
                final double average = samples.stream().mapToDouble(value -> value).average().orElse(1);
                final double speed = 50 / average;

                final double deviation = MathUtil.getStandardDeviation(samples);

                if (speed <= 0.82 && deviation < 50.0) {
                    if (increaseBuffer() > 35) {
                        fail(String.format("speed=%.2f deviation=%.2f", speed, deviation));
                    }
                } else {
                    multiplyBuffer(0.75);
                }
            }
            lastFlyingTime = now;
        }
    }
}
