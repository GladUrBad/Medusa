package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.RotationProcessor;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.function.Predicate;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (A)", description = "Checks for irregular movements in the rotation.")
public final class AimAssistA extends Check {

    private final Predicate<Float> validRotation = rotation -> rotation > 3F && rotation < 35F;

    public AimAssistA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = Math.abs(data.getRotationProcessor().getDeltaPitch());
            final float deltaYaw =  Math.abs(data.getRotationProcessor().getDeltaYaw() % 360F);
            final float pitch = Math.abs(data.getRotationProcessor().getPitch());

            final boolean invalidPitch = deltaPitch < 0.009 && validRotation.test(deltaYaw);
            final boolean invalidYaw = deltaYaw < 0.009 && validRotation.test(deltaPitch);

            final boolean exempt = isExempt(ExemptType.INSIDE_VEHICLE);

            final boolean invalid = !exempt && (invalidPitch || invalidYaw) && pitch < 89F;

            debug(String.format("deltaYaw=%.2f, deltaPitch=%.2f", deltaYaw, deltaPitch));

            if (invalid) {
                if (++buffer > 20) {
                    fail(String.format("deltaYaw=%.2f, deltaPitch=%.2f", deltaYaw, deltaPitch));
                }
            } else {
                buffer -= buffer > 0 ? 1 : 0;
            }
        }
    }
}
