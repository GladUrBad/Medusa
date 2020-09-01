package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

@CheckInfo(name = "Motion", type = "E", dev = true)
public class MotionE extends Check {

    private final double STEP_HEIGHT = 0.6F;

    public MotionE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && isFlyingPacket(packet)) {
            final boolean validToCheck = CollisionUtil.isOnGround(data.getLastLocation(), -0.5001) && CollisionUtil.isOnGround(data.getLocation(), -0.5001);
            if(validToCheck && data.getDeltaY() > STEP_HEIGHT) fail();
        }
    }
}
