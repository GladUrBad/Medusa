package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.speed by GladUrBad
 */
@CheckInfo(name = "Motion (E)", description = "Checks for switching direction mid-air.")
public final class MotionE extends Check {

    public MotionE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final double absDeltaX = Math.abs(deltaX);
            final double absDeltaZ = Math.abs(deltaZ);
            final double absLastDeltaX = Math.abs(lastDeltaX);
            final double absLastDeltaZ = Math.abs(lastDeltaZ);

            if (data.getPositionProcessor().getAirTicks() > 2 && !isExempt(ExemptType.VELOCITY, ExemptType.NEAR_VEHICLE, ExemptType.TELEPORT, ExemptType.FLYING)) {
                final boolean xSwitched = (deltaX > 0 && lastDeltaX < 0) || (deltaX < 0 && lastDeltaX > 0);
                final boolean zSwitched = (deltaZ > 0 && lastDeltaZ < 0) || (deltaZ < 0 && lastDeltaZ > 0);

                if (xSwitched) {
                    if (Math.abs(absDeltaX - absLastDeltaX) > 0.05) {
                        if (++buffer > 1.25) {
                            fail();
                        }
                    }
                } else {
                    buffer = Math.max(buffer - 0.05, 0);
                }
                if (zSwitched) {
                    if (Math.abs(absDeltaZ - absLastDeltaZ) > 0.05) {
                        if (++buffer > 1.25) {
                            fail();
                        }
                    }
                } else {
                    buffer = Math.max(buffer - 0.05, 0);
                }
            }
        }
    }
}
