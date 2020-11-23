package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;

import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 *
 * This check checks for abnormally high aim accuracy. Usually killaura's get above 95% consistently.
 * A legit player hovers around 0-80%. This check is easily falsed. I should probably make sure the players are moving
 * and looking around. But this has the basic concept.
 */

@CheckInfo(name = "KillAura (D)", experimental = true, description = "Checks for high accuracy.")
public class KillAuraD extends Check {


    public KillAuraD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final boolean invalid = data.getCombatProcessor().getHitMissRatio() > 98 &&
                    data.getRotationProcessor().getDeltaYaw() > 1.5F &&
                    data.getRotationProcessor().getDeltaPitch() > 0 &&
                    data.getPositionProcessor().getDeltaXZ() > 0.1;

            if (invalid) {
                if (increaseBuffer() > 25) {
                    fail("accuracy=" + data.getCombatProcessor().getHitMissRatio() + " buffer=" + getBuffer());
                }
            } else {
                decreaseBufferBy(2);
            }
        }
    }
}
