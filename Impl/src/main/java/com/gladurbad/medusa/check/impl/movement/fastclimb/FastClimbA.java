package com.gladurbad.medusa.check.impl.movement.fastclimb;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.movement.fastclimb by GladUrBad
 */

@CheckInfo(name = "FastClimb (A)", description = "Checks if the player exceeds maximum ladder movement speed.")
public class FastClimbA extends Check {

    public FastClimbA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final boolean onGround = data.getPositionProcessor().isOnSolidGround();
            final boolean onLadder = data.getPositionProcessor().isOnClimbable();
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double deltaDeltaY = Math.abs(data.getPositionProcessor().getDeltaY() - data.getPositionProcessor().getLastDeltaY());
            //Creates an easy bypass. Replace this with collisions replaced from MCP.
            final boolean invalid = !onGround && onLadder && deltaDeltaY == 0 && deltaY > 0.1177;

            /*
            * Using a buffer makes this check VERY easy to bypass.
            * This check should flag on the first tick. If you do not flag on the first tick, you run
            * the risk of insanely fast FastClimb bypasses. FIX THIS.
            */
            if (invalid) {
                if (increaseBuffer() > 2) {
                    fail(String.format("dy=%.2f", deltaY));
                }
            } else {
                decreaseBuffer(0.5);
            }
        }
    }
}
