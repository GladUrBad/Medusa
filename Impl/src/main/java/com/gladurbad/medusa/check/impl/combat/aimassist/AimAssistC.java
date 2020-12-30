package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.RotationProcessor;
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
        if (packet.isRotation() && !isExempt(ExemptType.TELEPORT)) {
            final RotationProcessor rots = data.getRotationProcessor();

            final float deltaYaw = rots.getDeltaYaw();
            final double yawAccel = rots.getYawAccel();
            final float deltaPitch = rots.getDeltaPitch();
            final double pitchAccel = rots.getPitchAccel();

            final boolean invalidYaw = yawAccel < 0.1 && Math.abs(deltaYaw) > 1.5F;
            final boolean invalidPitch = pitchAccel < 0.1 && Math.abs(deltaPitch) > 1.5F;

            if (invalidPitch || invalidYaw) {
                if (increaseBuffer() > 8) {
                    fail(String.format("dY=%.2f, dP=%.2f, yA=%.2f, pA=%.2f", deltaYaw, deltaPitch, yawAccel, pitchAccel));
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
