package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Speed (A)", description = "Checks for horizontal friction.")
public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double prediction = lastDeltaXZ * 0.91F + (data.getActionProcessor().isSprinting() ? 0.0263 : 0.02);
            final double difference = deltaXZ - prediction;

            final boolean invalid = difference > 1E-12 &&
                    data.getPositionProcessor().getAirTicks() > 2 &&
                    !data.getPositionProcessor().isFlying() &&
                    !data.getPositionProcessor().isNearBoat();

            if (invalid) {
                if (increaseBuffer() > 2.5) {
                    fail("prediction=" + prediction + " actual=" + deltaXZ + " difference=" + difference);
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
