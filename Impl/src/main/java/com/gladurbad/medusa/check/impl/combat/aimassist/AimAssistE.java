package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
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
                final float yaw = data.getRotationProcessor().getYaw() % 360F;
                final float lastYaw = data.getRotationProcessor().getLastYaw() % 360F;

                final float pitch = data.getRotationProcessor().getPitch();
                final float lastPitch = data.getRotationProcessor().getLastPitch();

                final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
                final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

                final long gcd = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

                final boolean cinematic = data.getRotationProcessor().isCinematic();
                final boolean check = yaw != lastYaw && pitch != lastPitch && !cinematic && deltaPitch < 20F;

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
