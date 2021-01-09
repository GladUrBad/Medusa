package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.speed by GladUrBad
 */

@CheckInfo(name = "Speed (A)", description = "Checks for horizontal friction.")
public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaXZ = positionInfo().getDeltaXZ();
            final double lastDeltaXZ = positionInfo().getLastDeltaXZ();

            final double prediction = lastDeltaXZ * 0.91F + (actionInfo().isSprinting() ? 0.0263 : 0.02);
            final double difference = deltaXZ - prediction;

            final boolean invalid = difference > 1E-12 &&
                    positionInfo().getAirTicks() > 2 &&
                    !positionInfo().isFlying() &&
                    !positionInfo().isNearBoat();

            if (invalid) {
                if (++buffer > 2.5) {
                    fail(String.format("diff=%.6f", difference));
                }
            } else {
                buffer = Math.max(buffer - 0.25, 0);
            }
        }
    }
}
