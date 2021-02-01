package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.Medusa;
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

@CheckInfo(name = "Timer (A)", description = "Checks for game speed which is too fast.")
public final class TimerA extends Check {

    private static final ConfigValue maxTimerSpeed = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-timer-speed");
    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlyingTime;

    public TimerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TPS)) {
            final long now = now();
            final long delta = now - lastFlyingTime;

            if (delta > 1) {
                samples.add(delta);
            }

            if (samples.isFull()) {
                final double average = MathUtil.getAverage(samples);
                final double speed = 50 / average;

                debug(String.format("speed=%.4f, delta=%o, buffer=%.2f", speed, delta, buffer));

                if (speed >= maxTimerSpeed.getDouble()) {
                    if (++buffer > 30) {
                        buffer = Math.min(buffer, 50);
                        fail(String.format("speed=%.4f, delta=%o, buffer=%.2f", speed, delta, buffer));
                    }
                } else {
                    buffer = Math.max(0, buffer - 1);
                }
            }

            lastFlyingTime = now;
        } else if (packet.isOutPosition()) {
            samples.add(135L); //Magic value. 100L doesn't completely fix it for some reason.
        }
    }
}
