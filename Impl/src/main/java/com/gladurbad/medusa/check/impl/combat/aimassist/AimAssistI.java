package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "AimAssist (I)", description = "Checks for snappy rotations.", experimental = true)
public class AimAssistI extends Check {

    private float lastDeltaYaw, lastLastDeltaYaw;

    public AimAssistI(final PlayerData data) {
        super(data);
    }
    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            debug("dy=" + deltaYaw + " ldy=" + lastDeltaYaw + " lldy=" + lastLastDeltaYaw);

            if (deltaYaw < 5F && lastDeltaYaw > 20F && lastLastDeltaYaw < 5F) {
                final double low = (deltaYaw + lastLastDeltaYaw) / 2;
                final double high = lastDeltaYaw;

                fail(String.format("low=%.2f, high=%.2f", low, high));
            }

            lastLastDeltaYaw = lastDeltaYaw;
            lastDeltaYaw = deltaYaw;
        }
    }
}
