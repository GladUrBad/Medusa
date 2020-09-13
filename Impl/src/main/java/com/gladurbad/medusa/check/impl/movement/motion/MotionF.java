package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;
import org.bukkit.util.Vector;

@CheckInfo(name = "Motion", type = "F", dev = true)
public class MotionF extends Check {

    private int teleportTicks;

    public MotionF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (isFlyingPacket(packet)) {
                if (data.isSprinting() && data.getTicksSinceVelocity() > 15 && ++teleportTicks > 5) {
                    final double deltaX = data.getLocation().getX() - data.getLastLocation().getX();
                    final double deltaZ = data.getLocation().getZ() - data.getLastLocation().getZ();

                    final double directionX = -Math.sin(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;
                    final double directionZ = Math.cos(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;

                    final Vector positionDifference = new Vector(deltaX, 0, deltaZ);
                    final Vector direction = new Vector(directionX, 0, directionZ);

                    final double angle = positionDifference.distanceSquared(direction);

                    if (angle > 0.32) {
                        increaseBuffer();
                        if (buffer > 10) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(2);
                        setLastLegitLocation(data.getLocation());
                    }
                }
            }
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleportTicks = 0;
        }
    }
}
