package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.speed by GladUrBad
 */

@CheckInfo(name = "Speed (A)", description = "Checks for horizontal friction.")
public final class SpeedA extends Check {

    public SpeedA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double prediction = lastDeltaXZ * 0.91F + (data.getActionProcessor().isSprinting() ? 0.0263 : 0.02);
            final double difference = deltaXZ - prediction;

            final boolean invalid = difference > 1e-12 &&
                    data.getPositionProcessor().getAirTicks() > 2 &&
                    !data.getPositionProcessor().isFlying() &&
                    !data.getPositionProcessor().isNearVehicle();

            debug("diff=" + difference);
            if (invalid) {
                if ((buffer += buffer < 100 ? 5 : 0) > 40) {
                    fail(String.format("diff=%.4f", difference));
                }
            } else {
                buffer = Math.max(buffer - 1, 0);
            }
        }
    }
}
