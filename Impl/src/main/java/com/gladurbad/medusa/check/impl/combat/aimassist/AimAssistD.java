package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

/**
 * Created on <unknown date> Package xyz.elevated.frequency.check.impl.aimassist by Elevated.
 *
 * This is one of the most powerful aim checks on Medusa. Just to clarify, it was created by Elevated, and taken from
 * Frequency with permission. This check makes sure each rotation has a GCD (greatest common divisor). Because of
 * how sensitivity works in Minecraft, every rotation is divisible by a specific constant. Because of this, I have
 * been able to get sensitivity from rotations in RotationProcesssor. If the rotation does not have a constant, this
 * check flags.
 *
 * @see com.gladurbad.medusa.data.processor.RotationProcessor
 *
 * https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/aimassist/AimAssistE.java
 */

@CheckInfo(name = "AimAssist (D)", description = "Checks for a divisor in the rotation.")
public class AimAssistD extends Check {

    private float lastDeltaYaw, lastDeltaPitch;

    public AimAssistD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = rotationInfo().getDeltaYaw() % 360F;
            final float deltaPitch = rotationInfo().getDeltaPitch();

            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
            final double divisorPitch = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

            final double constantYaw = divisorYaw / MathUtil.EXPANDER;
            final double constantPitch = divisorPitch / MathUtil.EXPANDER;

            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                if (invalidX && invalidY) {
                    if (++buffer > 6) {
                        fail(String.format(
                                "mx=%.2f, my=%.2f, fmx=%.2f, fmy=%.2f",
                                moduloX, moduloY, floorModuloX, floorModuloY
                        ));
                    }
                } else {
                    buffer = Math.max(0, buffer - 0.25);
                }
            }
            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;
        }
    }
}
