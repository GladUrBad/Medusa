package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "AutoClicker (D)", experimental = true, description = "Checks for low skewness values.")
public class AutoClickerD extends Check {

    private final ArrayDeque<Long> samples = new ArrayDeque<>();

    public AutoClickerD(final PlayerData data) {
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

            if (samples.size() >= 50) {
                final double skewness = MathUtil.getSkewness(samples);

                if (skewness < -0.01D) {
                    if (increaseBuffer() > 3) {
                        fail(String.format("skew=%.2f, buffer=%.2f", skewness, getBuffer()));
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
