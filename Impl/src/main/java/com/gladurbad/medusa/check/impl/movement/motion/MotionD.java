package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.util.Vector;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.motion by GladUrBad
 */

@CheckInfo(name = "Motion (D)", description = "Checks for sprint direction.")
public final class MotionD extends Check {

    private int teleportTicks;
    private int offGroundTicks;
    private final Vector direction = new Vector(0, 0, 0);

    public MotionD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (data.getActionProcessor().isSprinting() &&
                    data.getVelocityProcessor().getTicksSinceVelocity() > 15 &&
                    ++teleportTicks > 10 && !data.getPlayer().isFlying()) {
                final double deltaX = data.getPositionProcessor().getX() - data.getPositionProcessor().getLastX();
                final double deltaZ = data.getPositionProcessor().getZ() - data.getPositionProcessor().getLastZ();

                final double directionX = -Math.sin(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;
                final double directionZ = Math.cos(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;

                final Vector positionDifference = new Vector(deltaX, 0, deltaZ);

                if (data.getPlayer().isOnGround()) {
                    offGroundTicks = 0;
                    direction.setX(directionX);
                    direction.setY(0);
                    direction.setZ(directionZ);
                } else {
                    ++offGroundTicks;
                }

                final double angle = Math.toDegrees(positionDifference.angle(direction));

                final boolean invalid = !data.getPositionProcessor().isInLiquid()
                        && angle > 85
                        && data.getPositionProcessor().getDeltaXZ() > 0.25
                        && offGroundTicks < 8
                        && !isExempt(ExemptType.TELEPORT, ExemptType.VELOCITY);

                debug("angle=" + angle + " dxz=" + data.getPositionProcessor().getDeltaXZ() + " buffer=" + buffer);
                if (invalid) {
                    if (++buffer >= 8) {
                        fail(String.format("angle=%.2f, buffer=%.2f", angle, buffer));
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                }
            }
        } else if (packet.isTeleport()) {
            teleportTicks = 0;
        }
    }
}
