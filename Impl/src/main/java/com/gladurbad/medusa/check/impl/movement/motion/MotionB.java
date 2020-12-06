package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.motion by GladUrBad
 */

@CheckInfo(name = "Motion (B)", description = "Checks for fast-fall cheats.")
public class MotionB extends Check {

    public MotionB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean invalid = deltaY < -3.92 &&
                    !isExempt(ExemptType.TELEPORT);

            if (invalid) fail(String.format("dy=%.2f", deltaY));
        }
    }
}
