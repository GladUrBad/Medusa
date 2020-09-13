package com.gladurbad.medusa.check.impl.movement.flight;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

@CheckInfo(name = "Flight", type = "B")
public class FlightB extends Check {

    private double lastLastDeltaY;
    private double airTicks;

    public FlightB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getDeltaY();
            final double lastDeltaY = data.getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;
            final double lastAcceleration = lastDeltaY - lastLastDeltaY;

            if (!CollisionUtil.isOnGround(data.getPlayer()) && !data.getPlayer().isFlying()) {
                if (++airTicks > 10) {
                    if (acceleration > 0.0 && lastAcceleration <= 0.0 && data.getTicksSinceVelocity() > 10) {
                        fail();
                    }
                }
            } else {
                setLastLegitLocation(data.getPlayer().getLocation());
                airTicks = 0;
            }

            lastLastDeltaY = lastDeltaY;
        }
    }
}
