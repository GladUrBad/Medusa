package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.type.EvictingList;

/**
 * Created on 11/15/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (G)" , description = "Checks for extremely smooth rotations.")
public class AimAssistG extends Check {

    private final EvictingList<Float> yawAccelSamples = new EvictingList<>(20);
    private final EvictingList<Float> pitchAccelSamples = new EvictingList<>(20);

    public AimAssistG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && isExempt(ExemptType.COMBAT)) {
            final float yawAccel = data.getRotationProcessor().getYawAccel();
            final float pitchAccel = data.getRotationProcessor().getPitchAccel();

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw() % 360F;

            yawAccelSamples.add(yawAccel);
            pitchAccelSamples.add(pitchAccel);

            if (yawAccelSamples.isFull() && pitchAccelSamples.isFull()) {
                final double yawAccelAverage = MathUtil.getAverage(yawAccelSamples);
                final double pitchAccelAverage = MathUtil.getAverage(pitchAccelSamples);

                final double yawAccelDeviation = MathUtil.getStandardDeviation(yawAccelSamples);
                final double pitchAccelDeviation = MathUtil.getStandardDeviation(pitchAccelSamples);

                final boolean exemptRotation = deltaYaw < 1.5F;
                final boolean averageInvalid = yawAccelAverage < 1 || pitchAccelAverage < 1 && !exemptRotation;
                final boolean deviationInvalid = yawAccelDeviation < 5 && pitchAccelDeviation > 5 && !exemptRotation;

                if (averageInvalid && deviationInvalid) {
                    buffer = Math.min(buffer + 1, 20);
                    if (buffer > 8) {
                        fail(String.format(
                                "yaa=%.2f, paa=%.2f, yad=%.2f, pad=%.2f",
                                yawAccelAverage, pitchAccelAverage, yawAccelDeviation, pitchAccelDeviation
                        ));
                    }
                } else {
                    buffer = Math.max(0, buffer - 0.5);
                }
            }
        }
    }
}
