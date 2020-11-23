package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

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

            final double expectedJumpMotion = 0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F);
            final boolean jumped = deltaY > 0 && lastPosY % (1D/64) == 0 && !onGround;

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.VEHICLE,
                    ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDERBLOCK, ExemptType.PISTON,
                    ExemptType.LIQUID);

            if (jumped && !exempt) {
                if (deltaY < expectedJumpMotion) {
                    fail("jumped: dY=" + deltaY + " ejm=" + expectedJumpMotion);
                }
            }

            if (!exempt) {
                if (deltaY > expectedJumpMotion) {
                    fail("dY=" + deltaY + " > " + expectedJumpMotion);
                }
            }
        }
    }
}
