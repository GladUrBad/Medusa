package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.util.Vector;

@CheckInfo(name = "Velocity (B)", description = "Checks for horizontal velocity.", experimental = true)
public class VelocityB extends Check {

    private double lastDifference = -69;

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition() && data.getVelocityProcessor().getTicksSinceVelocity() <= 2) {
            final double velX = data.getVelocityProcessor().getVelocityX();
            final double velZ = data.getVelocityProcessor().getVelocityZ();

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final Vector movement = new Vector(deltaX, 0, deltaZ);
            final Vector velocity = new Vector(velX, 0, velZ);

            double realDifference = 0, difference = 0;

            if (lastDifference != -69) {
                difference = velocity.subtract(movement).length();
                realDifference = Math.min(difference, lastDifference);
            }

            if (realDifference > 0.26) {
                if (increaseBuffer() > 5) {
                    //fail("difference=" + realDifference);
                }
            } else {
                multiplyBuffer(0.6);
            }

            lastDifference = difference;
        }
    }
}
