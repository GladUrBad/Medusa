package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
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
        if (packet.isRotation()) {

            RotationProcessor rotationProcessor = data.getRotationProcessor();

            final double deltaYaw = rotationProcessor.getDeltaYaw();
            final double yawAccel = rotationProcessor.getYawAccel();
            final double deltaPitch = rotationProcessor.getDeltaPitch();
            final double pitchAccel = rotationProcessor.getPitchAccel();

            final boolean invalidYaw = yawAccel == 0 && Math.abs(deltaYaw) > 1.5F && !isExempt(ExemptType.TELEPORT);
            final boolean invalidPitch = pitchAccel == 0 && Math.abs(deltaPitch) > 1.5F && !isExempt(ExemptType.TELEPORT);

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
