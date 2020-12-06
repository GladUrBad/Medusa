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
public class FlyB extends Check {

    private double lastAcceleration;
    public FlyB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final double acceleration = data.getPositionProcessor().getDeltaY() - data.getPositionProcessor().getLastDeltaY();
            final double airTicks = data.getPositionProcessor().getAirTicks();
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean invalid = !this.isExempt(ExemptType.FLYING) &&
                    lastAcceleration <= 0 && acceleration > 0 && deltaY > 0;

            if (airTicks > 10) {
                if (invalid) {
                    fail(String.format("accel=%.2f, at=%.2f", acceleration, airTicks));
                }
            }

            lastAcceleration = acceleration;
        }
    }
}
