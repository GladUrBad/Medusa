package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (C)", description = "Checks for constant rotation.", experimental = true)
public final class AimAssistC extends Check {

    public AimAssistC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final double yawAccel = data.getRotationProcessor().getJoltYaw();
            final double pitchAccel = data.getRotationProcessor().getJoltPitch();

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.CINEMATIC);
            final boolean invalidYaw = yawAccel < 0.1 && Math.abs(deltaYaw) > 1.5F;
            final boolean invalidPitch = pitchAccel < 0.1 && Math.abs(deltaPitch) > 1.5F;

            final String info = String.format(
                    "dY=%.2f, dP=%.2f, yA=%.2f, pA=%.2f",
                    deltaYaw, deltaPitch, yawAccel, pitchAccel
            );

            debug(info);

            if ((invalidPitch || invalidYaw) && !exempt) {
                if (++buffer > 8) {
                    fail(info);
                }
            } else {
                buffer -= buffer > 0 ? 1 : 0;
            }
        }
    }
}
