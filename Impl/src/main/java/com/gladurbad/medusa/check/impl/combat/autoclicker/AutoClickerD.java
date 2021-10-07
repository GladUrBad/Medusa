package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.type.Pair;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by Elevated.
 * @link https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/autoclicker/AutoClickerE.java
 */

@CheckInfo(name = "AutoClicker (D)", experimental = true, description = "Checks for consistency.")
public final class AutoClickerD extends Check {

    private final ArrayDeque<Integer> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTO_CLICKER)) {
            if (ticks < 4) {
                samples.add(ticks);
            }

            if (samples.size() == 20) {
                final Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                final int outliers = outlierPair.getX().size() + outlierPair.getY().size();
                final int duplicates = (int) (samples.size() - samples.stream().distinct().count());

                debug("outliers=" + outliers + " dupl=" + duplicates);

                if (outliers < 2 && duplicates > 16) {
                    if ((buffer += 10) > 50) {
                        fail("outliers=" + outliers + " dupl=" + duplicates);
                    }
                } else {
                    buffer = Math.max(buffer - 8, 0);
                }
                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
