package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;

@CheckInfo(name = "Motion", type = "D")
public class MotionD extends Check {

    public MotionD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (MathUtil.isScientificNotation(Math.abs(data.getDeltaY()))) {
                increaseBuffer();
                if (buffer > 3) {
                    fail();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
