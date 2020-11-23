package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Motion (A)", experimental = true, description = "Checks for constant vertical movement.")
public class MotionA extends Check {

    public MotionA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double absDeltaY = Math.abs(data.getPositionProcessor().getDeltaY());
            final double absLastDeltaY = Math.abs(data.getPositionProcessor().getLastDeltaY());
            final double acceleration = absDeltaY - absLastDeltaY;

            final boolean invalid = acceleration == 0.0 &&
                    data.getPositionProcessor().getDeltaY() != 0.0 &&
                    data.getPositionProcessor().getLastDeltaY() != 0.0 &&
                    !data.getPositionProcessor().isInLiquid() &&
                    !data.getPositionProcessor().isOnSlime() &&
                    !data.getPositionProcessor().isOnClimbable() &&
                    !data.getPositionProcessor().isBlockNearHead();

            if (invalid) {
                if (increaseBuffer() > 2) {
                    fail("deltaY=" + absDeltaY);
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
