package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.util.Vector;

@CheckInfo(name = "Motion (D)", description = "Checks for sprint direction.")
public class MotionD extends Check {

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

                final boolean invalid = !data.getPositionProcessor().isInLiquid() &&
                        angle > 85 &&
                        data.getPositionProcessor().getDeltaXZ() > 0.25 &&
                        offGroundTicks < 8;

                if (invalid) {
                    if (increaseBuffer() >= 4) {
                        fail("angle (deg)=" + angle + " buffer=" + getBuffer());
                    }
                } else {
                    decreaseBufferBy(0.25);
                }
            }
        } else if (packet.isTeleport()) {
            teleportTicks = 0;
        }
    }
}
