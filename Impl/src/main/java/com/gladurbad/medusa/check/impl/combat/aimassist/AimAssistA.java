package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.RotationProcessor;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.function.Predicate;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (A)", description = "Checks for irregular movements in the rotation.")
public class AimAssistA extends Check {

    private final double minimumRotation = .009400162506103516;
    private final Predicate<Float> validDeltaYaw = yaw -> yaw > 2F && yaw < 35F;
    private final Predicate<Float> validDeltaPitch = pitch -> pitch > 2F && pitch < 35F;

    public AimAssistA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final RotationProcessor rots = data.getRotationProcessor();

            final float deltaPitch = Math.abs(rots.getDeltaPitch());
            final float deltaYaw =  Math.abs(rots.getDeltaYaw() % 360F);
            final float pitch = Math.abs(rots.getPitch());

            final boolean invalidPitch = deltaPitch < minimumRotation && validDeltaYaw.test(deltaYaw) && pitch < 89.9F;
            final boolean invalidYaw = deltaYaw < minimumRotation && validDeltaPitch.test(deltaPitch);

            if (invalidPitch || invalidYaw) {
                if (increaseBuffer() > 10) {
                    fail(String.format("deltaYaw=%.2f, deltaPitch=%.2f", deltaYaw, deltaPitch));
                }
            } else {
                decreaseBuffer(2);
            }
        }
    }
}
