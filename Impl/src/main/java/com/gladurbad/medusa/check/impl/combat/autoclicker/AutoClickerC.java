package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "AutoClicker (C)", experimental = true, description = "Checks for rounded(ish) CPS.")
public class AutoClickerC extends Check {

    private final ArrayDeque<Integer> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTO_CLICKER)) {

           if (ticks < 4) {
               samples.add(ticks);
           }

           if (samples.size() == 50) {
               final double cps = MathUtil.getCps(samples);
               final double difference = Math.abs(Math.round(cps) - cps);

               if (difference < 0.001) {
                   if ((buffer += 10) > 25) {
                       fail("diff=" + difference);
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
