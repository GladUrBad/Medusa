package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.entity.Player;

@CheckInfo(name = "Speed (B)", description = "Checks for movement speed.")
public class SpeedB extends Check {


    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());

            final boolean onGround = data.getPositionProcessor().isMathematicallyOnGround();
            final boolean blockNearHead = data.getPositionProcessor().isBlockNearHead();

            double maxGroundSpeed = PlayerUtil.getBaseGroundSpeed(data.getPlayer());
            double maxAirSpeed = PlayerUtil.getBaseSpeed(data.getPlayer());

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int sinceIceTicks = data.getPositionProcessor().getSinceIceTicks();

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.FLYING);
            final boolean velocityExempt = isExempt(ExemptType.VELOCITY);

            if (velocityExempt) {
                final double vX = data.getVelocityProcessor().getLastVelocityX();
                final double vZ = data.getVelocityProcessor().getLastVelocityZ();
                final double tV = Math.hypot(vX, vZ);
                maxAirSpeed += tV + 0.5;
                maxGroundSpeed += tV + 0.5;
            }

            if (groundTicks <= 7) {
                maxGroundSpeed += 0.15;
            }

            if (blockNearHead) {
                maxAirSpeed += 0.15;
                maxGroundSpeed += 0.075;
            }

            if (sinceIceTicks <= 10) {
                maxAirSpeed += 0.15;
                maxGroundSpeed += 0.1;
            }

            if (!exempt) {
                if (!onGround) {
                    if (deltaXZ > maxAirSpeed) {
                        if (increaseBuffer() > 2.75) {
                            fail("dxz=" + deltaXZ + " mas=" + maxAirSpeed);
                            multiplyBuffer(0.5);
                        }
                    } else {
                        decreaseBufferBy(0.5);
                    }
                }

                if (onGround) {
                    if (deltaXZ > maxGroundSpeed) {
                        if (increaseBuffer() > 2) {
                            fail("dxz=" + deltaXZ + " mgs=" + maxGroundSpeed);
                        }
                    } else {
                        decreaseBufferBy(0.5);
                    }
                }
            }
        }
    }
}
