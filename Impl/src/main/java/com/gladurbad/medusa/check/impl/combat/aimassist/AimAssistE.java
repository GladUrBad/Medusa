package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created 11/14/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (E)", description = "Checks for a valid sensitivity in the rotation.")
public class AimAssistE extends Check {

    public AimAssistE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final int sens = data.getRotationProcessor().getSensitivity();
            final float yawAccel = data.getRotationProcessor().getYawAccel();

            if (isExempt(ExemptType.COMBAT)) {
                if (yawAccel > 0.1F && sens < 0) {
                    if (increaseBuffer() > 10) {
                        fail("sens=" + sens + " yawAccel=" + yawAccel + " buffer=" + getBuffer());
                    }
                } else {
                    decreaseBufferBy(0.75);
                }
            }
        }
    }
}
