package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import com.gladurbad.medusa.util.CollisionUtil;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.util.Vector;

@CheckInfo(name = "Motion", type = "F", dev = true)
public class MotionF extends Check {

    private int teleportTicks;
    private int offGroundTicks;
    private Vector direction = new Vector(0, 0, 0);

    public MotionF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (data.isSprinting() && data.getTicksSinceVelocity() > 15 && ++teleportTicks > 5 && !data.getPlayer().isFlying()) {
                final double deltaX = data.getLocation().getX() - data.getLastLocation().getX();
                final double deltaZ = data.getLocation().getZ() - data.getLastLocation().getZ();

                final double directionX = -Math.sin(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;
                final double directionZ = Math.cos(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;

                final Vector positionDifference = new Vector(deltaX, 0, deltaZ);

                if (data.getPlayer().isOnGround()) {
                    offGroundTicks = 0;
                    direction.setX(directionX);
                    direction.setY(0);
                    direction.setZ(directionZ);
                }

                final double angle = positionDifference.angle(direction);

                final boolean invalid = !CollisionUtil.isInLiquid(data.getPlayer()) &&
                        angle > 1.56 &&
                        data.getDeltaXZ() > 0.25;

                if (invalid) {
                    if (increaseBuffer() >= 7) {
                        fail();
                        setBuffer(0);
                    }
                } else {
                    decreaseBufferBy(1.5);
                    setLastLegitLocation(data.getBukkitLocation());
                }
            }
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleportTicks = 0;
        }
    }
}
