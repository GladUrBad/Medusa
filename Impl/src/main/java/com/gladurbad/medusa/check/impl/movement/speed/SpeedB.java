package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.speed by GladUrBad
 */

@CheckInfo(name = "Speed (B)", description = "Checks for movement speed.")
public class SpeedB extends Check {


    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());
            final double lastDeltaXz = Math.abs(data.getPositionProcessor().getLastDeltaXZ());
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final boolean onGround = data.getPositionProcessor().isMathematicallyOnGround();
            final boolean blockNearHead = data.getPositionProcessor().isBlockNearHead();

            double maxGroundSpeed = PlayerUtil.getBaseGroundSpeed(data.getPlayer());
            double maxAirSpeed = PlayerUtil.getBaseSpeed(data.getPlayer());

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int airTicks = data.getPositionProcessor().getAirTicks();
            final int sinceIceTicks = data.getPositionProcessor().getSinceIceTicks();
            final int sinceSlimeTicks = data.getPositionProcessor().getSinceSlimeTicks();

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

            if (lastDeltaY <= 0 && deltaY > 0) {
                maxAirSpeed += 0.25;
                maxGroundSpeed += 0.4;
            }

            if (airTicks == 0) {
                 maxAirSpeed += 0.3;
            }
            if (blockNearHead) {
                maxAirSpeed += 0.17;
                maxGroundSpeed += 0.095;
            }

            if (sinceIceTicks <= 15) {
                maxAirSpeed += 0.2;
                maxGroundSpeed += 0.15;
            }

            if (sinceSlimeTicks <= 10) {
                maxAirSpeed += 0.2;
            }

            /*if (data.getVelocityProcessor().getMaxBukkitVelocityXz() > 0) {
                maxAirSpeed += data.getVelocityProcessor().getMaxBukkitVelocityXz();
                maxGroundSpeed += data.getVelocityProcessor().getMaxBukkitVelocityXz();
            }

            debug(data.getVelocityProcessor().getMaxBukkitVelocityXz() + " " + data.getVelocityProcessor().getMaxBukkitVelocityY());*/

            if (!exempt) {
                if (!onGround) {
                    if (deltaXZ > maxAirSpeed) {
                        if (increaseBuffer() > 1.25) {
                            if (getBuffer() > 10) {
                                decreaseBufferBy(getBuffer() - 10);
                            }
                            fail(String.format("dxz=%.2f, ldxz=%.2f, mas=%.2f, dy=%.2f, ldy=%.2f buffer=%.2f, at=%d", deltaXZ, lastDeltaXz, maxAirSpeed, deltaY, lastDeltaY, getBuffer(), airTicks));
                        }
                    } else {
                        decreaseBufferBy(0.025);
                    }
                }

                if (onGround) {
                    if (deltaXZ > maxGroundSpeed) {
                        if (increaseBuffer() > 1.25) {
                            if (getBuffer() > 5) {
                                decreaseBufferBy(getBuffer() - 5);
                            }
                            fail(String.format("dxz=%.2f, ldxz=%.2f, mgs=%.2f, dy=%.2f, ldy=%.2f buffer=%.2f, at=%d", deltaXZ, lastDeltaXz, maxGroundSpeed, deltaY, lastDeltaY, getBuffer(), airTicks));
                        }
                    } else {
                        decreaseBufferBy(0.025);
                    }
                }
            }
        }
    }
}
