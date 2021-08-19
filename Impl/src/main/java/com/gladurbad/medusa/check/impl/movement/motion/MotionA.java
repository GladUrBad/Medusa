package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.motion by GladUrBad
 */

@CheckInfo(name = "Motion (A)", description = "Checks for constant vertical movement.")
public final class MotionA extends Check {

    public MotionA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final boolean inAir = data.getPositionProcessor().isInAir();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = Math.abs(deltaY - lastDeltaY);

            final boolean exempt = isExempt(
                    ExemptType.JOINED, ExemptType.TRAPDOOR, ExemptType.VELOCITY,
                    ExemptType.FLYING, ExemptType.WEB, ExemptType.TELEPORT,
                    ExemptType.LIQUID, ExemptType.SLIME, ExemptType.CLIMBABLE,
                    ExemptType.UNDER_BLOCK, ExemptType.SLAB, ExemptType.STAIRS,
                    ExemptType.NEAR_VEHICLE
            );

            if (acceleration == 0.0 && inAir && !exempt) {
                if ((buffer += 4) > 16) {
                    fail(String.format("dy=%.2f", deltaY));
                }
            } else {
                buffer = Math.max(buffer - 1, 0);
            }
        }
    }
}
