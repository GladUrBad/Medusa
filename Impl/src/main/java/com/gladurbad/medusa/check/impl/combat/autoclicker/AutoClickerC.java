package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "AutoClicker (C)", experimental = true, description = "Checks for rounded(ish) CPS.")
public class AutoClickerC extends Check {

    private final ArrayDeque<Long> samples = new ArrayDeque<>();

    public AutoClickerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            final long delay = data.getClickProcessor().getDelay();

            if (delay > 5000L) {
                samples.clear();
                return;
            }

            samples.add(delay);

            if (samples.size() >= 20) {
                final double cps = MathUtil.getCps(samples);
                final double difference = Math.abs(Math.round(cps) - cps);

                if (difference < 0.08D) {
                    if (increaseBuffer() > 3) {
                        fail(String.format("difference=%.2f, buffer=%.2f", difference, getBuffer()));
                        multiplyBuffer(0.5);
                    }
                } else {
                    decreaseBufferBy(0.5);
                }

                samples.clear();
            }
        }
    }
}
