package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 */

@CheckInfo(name = "Fly (A)", description = "Checks for gravity.")
public class FlyA extends Check {

    private boolean invalid;

    public FlyA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double prediction = (lastDeltaY - 0.08) * 0.9800000190734863;
            final double difference = Math.abs(deltaY - prediction);

            final boolean invalid = difference > 0.005D &&
                    //Retarded collision processor makes me do dumb shit that could make bypasses like this.
                    !(data.getPositionProcessor().getY() % 0.5 == 0 && data.getPositionProcessor().isOnGround() && lastDeltaY < 0) &&
                    data.getPositionProcessor().isInAir() &&
                    !data.getPositionProcessor().isNearBoat() &&
                    !data.getPlayer().isFlying() &&
                    !data.getPlayer().isInsideVehicle() &&
                    data.getPositionProcessor().getAirTicks() > 15 &&
                    !data.getVelocityProcessor().isTakingVelocity() &&
                    Math.abs(prediction) > 0.005;

            if (invalid) {
                if (increaseBuffer() > 4) {
                    fail(String.format("diff=%.4f, buffer=%.2f", difference, getBuffer()));
                }
            } else {
                decreaseBufferBy(0.035);
            }
        }
    }
}
