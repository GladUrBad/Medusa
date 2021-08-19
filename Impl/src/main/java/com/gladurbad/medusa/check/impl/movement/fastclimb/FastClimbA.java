package com.gladurbad.medusa.check.impl.movement.fastclimb;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.movement.fastclimb by GladUrBad
 */

@CheckInfo(name = "FastClimb (A)", description = "Checks if the player exceeds maximum ladder movement speed.")
public final class FastClimbA extends Check {

    public FastClimbA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition() && !isExempt(ExemptType.TELEPORT)) {
            final boolean onGround = data.getPositionProcessor().isOnSolidGround();
            final boolean onLadder = data.getPositionProcessor().isOnClimbable();
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double deltaDeltaY = Math.abs(data.getPositionProcessor().getDeltaY() - data.getPositionProcessor().getLastDeltaY());
            final double maximumNonBufferDeltaY = 0.42F + PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
            //Creates an easy bypass. Replace this with collisions replaced from MCP.
            final boolean invalid = (!onGround && onLadder && deltaDeltaY == 0 && deltaY > 0.1177);
            final boolean fuckOff = deltaY > maximumNonBufferDeltaY && onLadder;

            /*
            * Using a buffer makes this check VERY easy to bypass.
            * This check should flag on the first tick. If you do not flag on the first tick, you run
            * the risk of insanely fast FastClimb bypasses. FIX THIS.
            */

            debug("dy=" + deltaY);
            if (invalid) {
                if (++buffer > 2) {
                    fail(String.format("dy=%.2f > 0.1176", deltaY));
                }
            } else {
                buffer = Math.max(buffer - 0.5, 0);
            }

            //it seems like on ladders the players velocity is the deltaY he puts out. not fully tested
            if (data.getVelocityProcessor().isTakingVelocity()) {

                double velocityDiff = Math.abs(deltaY - data.getVelocityProcessor().getVelocityY());

              if (velocityDiff > 0.0000001 && fuckOff) {
                  fail("dy=" + deltaY + " mnbdy=" + maximumNonBufferDeltaY + " vy=" + data.getVelocityProcessor().getVelocityY() +
                          " vd=" + velocityDiff);
              }
            } else if (fuckOff) fail("dy=" + deltaY + " mnbdy=" + maximumNonBufferDeltaY + " vy=" + data.getVelocityProcessor().getVelocityY());
        }
    }
}
