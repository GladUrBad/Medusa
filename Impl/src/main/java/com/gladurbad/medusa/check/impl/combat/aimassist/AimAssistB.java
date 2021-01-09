package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (B)", description = "Checks for rounded rotation.")
public class AimAssistB extends Check {

    public AimAssistB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && !isExempt(ExemptType.TELEPORT)) {
            final float deltaYaw = rotationInfo().getDeltaYaw() % 360F;
            final float deltaPitch = rotationInfo().getDeltaPitch();

            final boolean invalid = (deltaPitch % 1 == 0 || deltaYaw % 1 == 0) && deltaPitch != 0 && deltaYaw != 0;

            if (invalid) {
                if (++buffer > 4) {
                    fail(String.format("buffer=%.2f", buffer));
                }
            } else {
                buffer = Math.max(0, buffer - 0.25);
            }
        }
    }
}
