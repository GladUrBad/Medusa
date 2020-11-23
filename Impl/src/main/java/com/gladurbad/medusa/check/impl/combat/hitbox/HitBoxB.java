package com.gladurbad.medusa.check.impl.combat.hitbox;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.type.Pair;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.combat.reach by GladUrBad
 *
 * You have to be looking at the player to hit it!
 * Optimize this check and make it a little more effective.
 */

@CheckInfo(name = "HitBox (B)", description = "Checks for the angle of the attack.")
public class HitBoxB extends Check {

    public HitBoxB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());

            final double deltaX = useEntity.getEntity().getLocation().getX() - data.getPlayer().getLocation().getX();
            final double deltaZ = useEntity.getEntity().getLocation().getZ() - data.getPlayer().getLocation().getZ();

            final double directionX = -Math.sin(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;
            final double directionZ = Math.cos(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;

            final Vector direction = new Vector(directionX, 0, directionZ);
            final Vector positionDifference = new Vector(deltaX, 0, deltaZ);

            final double diffX = positionDifference.getX();
            final double diffZ = positionDifference.getZ();
            final double diffXz = Math.hypot(diffX, diffZ);

            final double angle = Math.toDegrees(positionDifference.angle(direction));
            final double maxAngle = getMaxAnglePerDistance(diffXz);

            final boolean invalid = angle > maxAngle;

            if (invalid) {
                if (increaseBuffer() > 4) {
                    fail(String.format("angle difference: %.2f > %.2f, buffer: %.2f", angle, maxAngle, getBuffer()));
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }

    // WHAT IS THIS AHHHHH
    // CAN SOMEONE DO MATH FOR ME PLEASE LMAO
    private static double getMaxAnglePerDistance(final double distance) {
        if (distance < 1) return Double.POSITIVE_INFINITY;
        if (distance > 1 && distance < 1.5) return 35D;
        if (distance > 1.5 && distance < 2) return 23D;
        if (distance > 2 && distance < 2.5) return 15D;
        if (distance > 2.5 && distance < 3) return 12D;
        if (distance > 3) return 10D;
        return Double.POSITIVE_INFINITY;
    }
}