package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Material;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 */

@CheckInfo(name = "Fly (A)", description = "Checks for gravity.")
public class FlyA extends Check {

    private int ticks;
    private long lastFlying;

    public FlyA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = positionInfo().getDeltaY();
            final double lastDeltaY = positionInfo().getLastDeltaY();

            final double prediction = (lastDeltaY - 0.08) * 0.98F;
            final double difference = Math.abs(deltaY - prediction);

            final boolean invalid = difference > 0.001D &&
                    //Retarded collision processor makes me do dumb shit that could make bypasses like this.
                    !(positionInfo().getY() % 0.5 == 0 && positionInfo().isOnGround() && lastDeltaY < 0) &&
                    positionInfo().isInAir() &&
                    !positionInfo().isNearBoat() &&
                    !player().isFlying() &&
                    !player().isInsideVehicle() &&
                    positionInfo().getAirTicks() > 15 &&
                    !velocityInfo().isTakingVelocity() &&
                    Math.abs(prediction) > 0.005;

            if (invalid) {
                if (++buffer > 5) {
                    fail(String.format("diff=%.4f, buffer=%.2f", difference, buffer));
                }
            } else {
                buffer = Math.max(buffer - 0.035, 0);
            }
        }
    }
}
