package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.type.EvictingList;

/**
 * Created on 11/13/2020 Package com.gladurbad.medusa.check.impl.player.timer by GladUrBad
 */

@CheckInfo(name = "Timer (B)",  description = "Checks for game speed which is too slow.", experimental = true)
public final class TimerB extends Check {

    private static final ConfigValue minSpeed = new ConfigValue(ConfigValue.ValueType.DOUBLE, "minimum-timer-speed");
    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlyingTime;


    public TimerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TPS)) {
            final long now = now();
            final long delta = now - lastFlyingTime;

            samples.add(delta);
            if (samples.isFull()) {
                final double average = samples.stream().mapToDouble(value -> value).average().orElse(1);
                final double speed = 50 / average;

                final double deviation = MathUtil.getStandardDeviation(samples);

                if (speed <= minSpeed.getDouble() && deviation < 50.0) {
                    if (++buffer > 35) {
                        fail(String.format("speed=%.2f deviation=%.2f", speed, deviation));
                    }
                } else {
                    buffer /= 2;
                }
            }
            lastFlyingTime = now;
        }
    }
}
