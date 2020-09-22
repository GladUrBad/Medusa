package com.gladurbad.medusa.check.impl.movement.flight;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import org.bukkit.Material;

@CheckInfo(name = "Flight", type = "A")
public class FlightA extends Check {

    private int vehicleTicks;
    private static final double LIMIT = 0.005;

    public FlightA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final double prediction = (data.getLastDeltaY() - 0.08) * 0.9800000190734863;
            final double difference = Math.abs(data.getDeltaY() - prediction);

            final boolean inVehicle = data.getPlayer().isInsideVehicle();

            if (inVehicle) {
                vehicleTicks = 0;
            } else {
                vehicleTicks++;
            }
            final boolean invalid =
                    difference > LIMIT &&
                            !CollisionUtil.isOnGround(data.getPlayer()) &&
                            !CollisionUtil.isOnChosenBlock(data.getPlayer(), -1.0,Material.SLIME_BLOCK) &&
                            !CollisionUtil.isNearBoat(data.getPlayer()) &&
                            !data.getPlayer().isFlying() &&
                            !data.isRiptiding() &&
                            !data.isGliding() &&
                            vehicleTicks > 20;
           if(invalid){
               data.getPlayer().sendMessage(difference * 100 + " > " + LIMIT * 100);
           }
            if (invalid) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
