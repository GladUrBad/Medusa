package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.RotationProcessor;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

/**
 * Created 12/06/2020 Package com.gladurbad.medusa.check.impl.combat.aimassist by GladUrBad
 */

@CheckInfo(name = "AimAssist (E)", description = "Checks for a valid sensitivity in the rotation.", experimental = true)
public class AimAssistE extends Check {

    public AimAssistE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            if (isExempt(ExemptType.COMBAT)) {

                RotationProcessor rotationProcessor = data.getRotationProcessor();

                final float yaw = rotationProcessor.getYaw() % 360F;
                final float lastYaw = rotationProcessor.getLastYaw() % 360F;

                final float pitch = rotationProcessor.getPitch();
                final float lastPitch = rotationProcessor.getLastPitch();

                final float deltaPitch = rotationProcessor.getDeltaPitch();
                final float lastDeltaPitch = rotationProcessor.getLastDeltaPitch();

                final long gcd = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

                final boolean cinematic = rotationProcessor.isCinematic();
                final boolean check = yaw != lastYaw && pitch != lastPitch && !cinematic;

                if (check) {
                    if (gcd < 131072L) { //131072L is the minimum rotation divisor you can get in Minecraft (except for Cinematic camera).
                        if (increaseBuffer() > 10) {
                            setBuffer(10);
                            fail("gcd=" + gcd + " buffer=" + getBuffer());
                        }
                    } else {
                        decreaseBuffer();
                    }
                }
            }
        }
    }
}
