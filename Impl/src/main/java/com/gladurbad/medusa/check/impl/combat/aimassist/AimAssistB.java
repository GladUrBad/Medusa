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
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw() % 360F;
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final boolean invalidPitch = deltaPitch % 1 == 0 && deltaPitch != 0F && !isExempt(ExemptType.TELEPORT);
            final boolean invalidYaw = deltaYaw % 1 == 0 && deltaYaw != 0F && !isExempt(ExemptType.TELEPORT);

            if (invalidPitch || invalidYaw) {
                if (increaseBuffer() > 4) {
                    fail(String.format("buffer=%.2f", getBuffer()));
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
