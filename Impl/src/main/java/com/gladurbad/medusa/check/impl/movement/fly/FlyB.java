package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 */

@CheckInfo(name = "Fly (B)", description = "Checks for jumping mid-air.")
public final class FlyB extends Check {

    private double lastAcceleration;

    public FlyB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition() && !isExempt(ExemptType.TELEPORT)) {
            final double acceleration = data.getPositionProcessor().getDeltaY() - data.getPositionProcessor().getLastDeltaY();
            final double airTicks = data.getPositionProcessor().getAirTicks();
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean invalid = !isExempt(
                    ExemptType.FLYING, ExemptType.VELOCITY, ExemptType.INSIDE_VEHICLE, ExemptType.NEAR_VEHICLE
            ) && lastAcceleration <= 0 && acceleration > 0 && deltaY > 0;

            debug("airTicks=" + airTicks + " accel=" + acceleration);
            if (airTicks > 10) {
                if (invalid) {
                    fail(String.format("accel=%.2f, at=%.2f", acceleration, airTicks));
                }
            }

            lastAcceleration = acceleration;
        }
    }
}
