package com.gladurbad.medusa.check.impl.movement.flight;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

@CheckInfo(name = "Flight", type = "A", dev = false)
public class FlightA extends Check {

    public FlightA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            final double prediction = (data.getLastDeltaY() * 0.9800000190734863) - 0.08;
            final double difference = Math.abs(data.getDeltaY() - prediction);

            final double EPSILON = 0.02;

            if (difference > EPSILON && !CollisionUtil.isOnGround(data.getPlayer()) && !CollisionUtil.isNearBoat(data.getPlayer())) {
                increaseBuffer();
                if (buffer > 5) {
                    failAndSetback();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
