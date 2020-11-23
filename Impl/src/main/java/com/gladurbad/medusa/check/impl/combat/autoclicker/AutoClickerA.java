package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.autoclicker by GladUrBad
 *
 * This check ensures the player does not click at an abnormally fast rate. That's about it.
 */

@CheckInfo(name = "AutoClicker (A)", description = "Checks for fast-clicking.")
public class AutoClickerA extends Check {

    private int ticks, cps;

    public AutoClickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.AUTOCLICKER)) {
            if (++ticks >= 20) {
                if (cps > 20) {
                    fail("cps=" + cps);
                }

                ticks = 0;
                cps = 0;
            }
        } else if (packet.isArmAnimation()) {
            ++cps;
        }
    }
}
