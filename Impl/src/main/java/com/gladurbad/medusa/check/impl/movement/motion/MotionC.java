package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

@CheckInfo(name = "Motion", type = "C")
public class MotionC extends Check {

    public MotionC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final double acceleration = Math.abs(data.getDeltaY() - data.getLastDeltaY());

            final boolean invalid =
                    acceleration == 0
                    && data.getDeltaY() != 0.0D
                    && data.getDeltaY() != 3.92
                    && !data.getPlayer().isFlying()
                    && !CollisionUtil.isCollidingWithClimbable(data.getPlayer())
                    && !CollisionUtil.isInLiquid(data.getPlayer());

            if (invalid) {
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