package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Fly (A)", description = "Checks for gravity.")
public class FlyA extends Check {

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
                    data.getPositionProcessor().isInAir() &&
                    !data.getPositionProcessor().isNearBoat() &&
                    !data.getPlayer().isFlying() &&
                    !data.getPlayer().isInsideVehicle() &&
                    data.getPositionProcessor().getAirTicks() > 3 &&
                    Math.abs(prediction) > 0.005;



            if (invalid) {
                if (increaseBuffer() > 3.5) {
                    fail("prediction=" + prediction + " deltaY=" + deltaY + " buffer=" + getBuffer());
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
