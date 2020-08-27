package com.gladurbad.medusa.check.impl.movement.fastclimb;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

@CheckInfo(name = "FastClimb", type = "A", dev = false)
public class FastClimbA extends Check {

    public FastClimbA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            if(CollisionUtil.isCollidingWithClimbable(data.getPlayer()) && data.getDeltaY() > 0.1177) {
                increaseBuffer();
                if (buffer > 5) {
                    fail();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
