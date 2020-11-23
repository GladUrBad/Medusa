package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (C)", description = "Checks for constant rotation.")
public class AimAssistC extends Check {

    public AimAssistC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final boolean invalidYaw = data.getRotationProcessor().getYawAccel() == 0 &&
                    Math.abs(data.getRotationProcessor().getDeltaYaw()) > 1.5F &&
                    !isExempt(ExemptType.TELEPORT);

            final boolean invalidPitch = data.getRotationProcessor().getPitchAccel() == 0 &&
                    Math.abs(data.getRotationProcessor().getDeltaPitch()) > 1.5F &&
                    !isExempt(ExemptType.TELEPORT);

            if (invalidPitch || invalidYaw) {
                if (increaseBuffer() > 8) {
                    fail("deltaYaw=" + data.getRotationProcessor().getDeltaYaw());
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
