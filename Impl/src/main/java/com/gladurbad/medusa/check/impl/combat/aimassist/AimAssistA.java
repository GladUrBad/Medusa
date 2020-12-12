package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.RotationProcessor;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (A)", description = "Checks for irregular movements in the rotation.")
public class AimAssistA extends Check {

    public AimAssistA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {

            RotationProcessor rotationProcessor = data.getRotationProcessor();

            final float deltaPitch = rotationProcessor.getDeltaPitch();
            final float deltaYaw = rotationProcessor.getDeltaYaw();
            final float pitch = rotationProcessor.getPitch();

            final boolean invalidPitch = (MathUtil.isExponentiallySmall(deltaPitch) ||
                    deltaPitch == 0F) && deltaYaw > 2F && Math.abs(pitch) < 85F && deltaYaw < 40F;

            final boolean invalidYaw = (MathUtil.isExponentiallySmall(deltaYaw) ||
                    deltaYaw == 0F) && deltaPitch > 2F && deltaPitch < 40F;

            if (invalidPitch || invalidYaw) {
                if (increaseBuffer() > 10) {
                    fail(String.format("deltaYaw=%.2f, deltaPitch=%.2f", deltaYaw, deltaPitch));
                }
            } else {
                resetBuffer();
            }
        }
    }
}
