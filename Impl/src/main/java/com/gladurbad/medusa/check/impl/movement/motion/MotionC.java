package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.motion by GladUrBad
 */

@CheckInfo(name = "Motion (C)", description = "Checks for jump height/vertical movement threshold.")
public class MotionC extends Check {

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

            final Vector vel = data.getPlayer().getVelocity();

            final double expectedJumpMotion = 0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F);
            final boolean jumped = deltaY > 0 && lastPosY % (1D/64) == 0 && !onGround && !step;

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.VEHICLE,
                    ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDERBLOCK, ExemptType.PISTON,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.WEB, ExemptType.TRAPDOOR);

            if (jumped && !exempt) {
                if (deltaY < expectedJumpMotion) {
                    fail(String.format("lowhop: dy=%.2f, expected=%.2f", deltaY, expectedJumpMotion));
                }
            }

            if (!exempt && !step) {
                if (deltaY > expectedJumpMotion) {
                    fail(String.format("highjump: dy=%.2f, max=%.2f, vx=%.2f, vy=%.2f, vz=%.2f", deltaY, expectedJumpMotion, vel.getX(), vel.getY(), vel.getZ()));
                }
            }

            if (step && !isExempt(ExemptType.TELEPORT)) {
                if (deltaY > 0.6F) {
                    fail(String.format("step: dy=%.2f, max=0.6", deltaY));
                }
            }
        }
    }
}
