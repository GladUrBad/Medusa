package com.gladurbad.medusa.check.impl.movement.flight;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

@CheckInfo(name = "Flight", type = "B", dev = true)
public class FlightB extends Check {

    private double lastLastDeltaY;
    private double airTicks;

    public FlightB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && this.isFlyingPacket(packet)) {
            final double deltaY = data.getDeltaY();
            final double lastDeltaY = data.getLastDeltaY();

            final double accel = deltaY - lastDeltaY;
            final double lastAccel = lastDeltaY - lastLastDeltaY;

            if(!CollisionUtil.isOnGround(data.getPlayer())) {
                airTicks = Math.min(200, airTicks + 1);
                if(airTicks > 10) {
                    if(accel > 0.0 && lastAccel <= 0.0 && data.getTicksSinceVelocity() > 10) {
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
