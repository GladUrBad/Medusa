package com.gladurbad.medusa.check.impl.combat.hitbox;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.util.Vector;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.combat.reach by GladUrBad
 */

@CheckInfo(name = "HitBox (B)", experimental = true, description = "Checks for the angle of the attack.")
public class HitBoxB extends Check {

    public HitBoxB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());

            final double deltaX = useEntity.getEntity().getLocation().getX() - player().getLocation().getX();
            final double deltaZ = useEntity.getEntity().getLocation().getZ() - player().getLocation().getZ();

            final double directionX = -Math.sin(player().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;
            final double directionZ = Math.cos(player().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;

            final Vector direction = new Vector(directionX, 0, directionZ);
            final Vector positionDifference = new Vector(deltaX, 0, deltaZ);

            final double diffX = positionDifference.getX();
            final double diffZ = positionDifference.getZ();
            final double diffXz = Math.hypot(diffX, diffZ);

            final double angle = Math.toDegrees(positionDifference.angle(direction));
            final double maxAngle = getMaxAnglePerDistance(diffXz);

            final boolean invalid = angle > maxAngle;

            if (invalid) {
                buffer = Math.min(buffer + 1, 20);
                if (buffer > 10) {
                    fail(String.format("angle difference: %.2f > %.2f, buffer: %.2f", angle, maxAngle, buffer));
                }
            } else {
                buffer = Math.max(buffer - 2, 0);
            }
        }
    }

    // Fix this using a raytrace util
    private static double getMaxAnglePerDistance(final double distance) {
        if (distance < 1.5) return Double.POSITIVE_INFINITY;
        if (distance > 1.5 && distance < 2) return 28D;
        if (distance > 2 && distance < 2.5) return 20D;
        if (distance > 2.5 && distance < 3) return 17D;
        if (distance > 3) return 15D;
        return Double.POSITIVE_INFINITY;
    }
}