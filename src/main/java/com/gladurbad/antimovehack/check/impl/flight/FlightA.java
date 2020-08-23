package com.gladurbad.antimovehack.check.impl.flight;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.network.Packet;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;

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
