package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

@CheckInfo(name = "Motion", type = "C", dev = true)
public class MotionC extends Check {

    public MotionC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            final double deltaY = Math.abs(data.getDeltaY());
            final double lastDeltaY = Math.abs(data.getLastDeltaY());

            final boolean invalid = deltaY == lastDeltaY && deltaY != 0.0D && deltaY != -3.92 && !CollisionUtil.isCollidingWithClimbable(data.getPlayer());

            if(invalid) {
                increaseBuffer();
                if(buffer > 3) {
                    fail();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}