package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.motion by GladUrBad
 */

@CheckInfo(name = "Motion (C)", description = "Checks for jump height/vertical movement threshold.")
public final class MotionC extends Check {

    public MotionC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastPosY = data.getPositionProcessor().getLastY();
            final boolean onGround = data.getPositionProcessor().isOnGround();

            final boolean step = MathUtil.mathOnGround(deltaY) && MathUtil.mathOnGround(lastPosY);

            final double expectedJumpMotion = 0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F);

            final double maxHighJump = 0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F) +
                    (data.getVelocityProcessor().getTicksSinceVelocity() < 5 ? data.getVelocityProcessor().getVelocityY() > 0 ? data.getVelocityProcessor().getVelocityY() : 0 : 0);

            final boolean jumped = deltaY > 0 && lastPosY % (1D/64) == 0 && !onGround && !step;

            final boolean exempt = isExempt(
                    ExemptType.INSIDE_VEHICLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDER_BLOCK,
                    ExemptType.PISTON, ExemptType.LIQUID, ExemptType.NEAR_VEHICLE, ExemptType.TELEPORT,
                    ExemptType.WEB, ExemptType.TRAPDOOR
            );

            debug("deltaY" + deltaY + " ejm=" + expectedJumpMotion + " mhj=" + maxHighJump + " step=" + step);
            //Mini-jump check.
            if (jumped && !exempt && !isExempt(ExemptType.VELOCITY)) {
                if (deltaY < expectedJumpMotion) {
                    fail(String.format("ASC-L: %.2f < %.2f", deltaY, expectedJumpMotion));
                }
            }

            //High-jump check.
            if (!exempt && !step) {
                //Patch for mid-air step glitch.
                if (deltaY > (data.getPositionProcessor().isOnGround() ? 0.6 : maxHighJump)) {
                    fail(String.format(
                            "ASC-H: %.2f > %.2f, onGround=%b",
                            deltaY, expectedJumpMotion, data.getPositionProcessor().isOnGround()
                    ));
                }
            }

            //Generic step check.
            if (step && !isExempt(ExemptType.TELEPORT)) {
                if (deltaY > 0.6F) {
                    fail(String.format("ASC-S: %.2f > 0.6", deltaY));
                }
            }
        }
    }
}
