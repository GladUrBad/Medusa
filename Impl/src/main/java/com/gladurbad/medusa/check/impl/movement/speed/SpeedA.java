package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created by Spiriten.
 * added since Medusa's previous would just exempt on velocity. This allowed for long jump bypasses.
 */

@CheckInfo(name = "Speed (A)", description = "Checks for horizontal friction.")
public final class SpeedA extends Check {

    public SpeedA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            if (!data.getPositionProcessor().isOnGround() && !data.getPositionProcessor().isLastOnGround()) {

                //stole the if Y thing is same as last from hades. it seems to prevent some niche false positives.
                if (data.getPlayer().isFlying() || (data.getPositionProcessor().getY() == data.getPositionProcessor().getLastY())
                        || isExempt(ExemptType.WEB, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.TELEPORT)) return;

                //afaik this is actually the first tick after teleport. it can false with teleport spam
                if (data.getPositionProcessor().getSinceTeleportTicks() < 2) return;

                //0.0259 is probably not entirely accurate, but thats the closest i got from debugging
                //while in the air the deltaXZ should reduce by 0.91x each packet. the 0.0259 is whats leftover from debugging
                //made it a floating point as Izibane likes to yell at me to make all minecraft variables them
                double prediction = data.getPositionProcessor().getLastDeltaXZ() * 0.91F + 0.0259F;
                double accuracy = (data.getPositionProcessor().getDeltaXZ() - prediction);

                //i could try to go for higher accuracy, but knowing me id mess something up and need a buffer. having no
                //buffer is better than accuracy for this type of check imo.
                if (accuracy > 0.001 && data.getPositionProcessor().getDeltaXZ() > 0.1) {
                    //if the player isnt taking knockback and doesnt meet our accuracy, then flag
                    if (!data.getVelocityProcessor().isTakingVelocity()) {
                        if (++buffer > 1.5) {
                            fail("exp=" + prediction + " got=" + data.getPositionProcessor().getDeltaXZ() + " tst="
                                    + data.getPositionProcessor().getSinceTeleportTicks());
                            //if the player is taking knockback, we have to account that into it. when in the air you only
                            //take the knockback, it resets all momentum as far as i know. so compare our velocityXZ to our
                            //deltaXZ. from my testing it can be off by about 0.04, so check if the margin is greater than that.
                        }
                    } else if (Math.abs(data.getVelocityProcessor().getVelocityXZ() -
                            data.getPositionProcessor().getDeltaXZ()) > 0.04 && data.getVelocityProcessor().isTakingVelocity()) {
                        fail("exp=" + prediction + " got=" + data.getPositionProcessor().getDeltaXZ() + " vel=" +
                                data.getVelocityProcessor().getVelocityXZ() + " tst="
                                + data.getPositionProcessor().getSinceTeleportTicks());
                    } else buffer = Math.max(buffer - 0.1, 0);
                } else buffer = Math.max(buffer - 0.1, 0);
            }
        }
    }
}