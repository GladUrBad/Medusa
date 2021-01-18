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

@CheckInfo(name = "Motion (A)", experimental = true, description = "Checks for constant vertical movement.")
public class MotionA extends Check {

    public MotionA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double absDeltaY = Math.abs(data.getPositionProcessor().getDeltaY());
            final double absLastDeltaY = Math.abs(data.getPositionProcessor().getLastDeltaY());
            final double acceleration = absDeltaY - absLastDeltaY;
            final Vector vel = data.getPlayer().getVelocity();

            final boolean invalid = acceleration == 0.0 &&
                     !isExempt(ExemptType.JOINED, ExemptType.TRAPDOOR, ExemptType.VELOCITY, ExemptType.FLYING, ExemptType.WEB) &&
                    data.getPositionProcessor().getDeltaY() != 0.0 &&
                    data.getPositionProcessor().getLastDeltaY() != 0.0 &&
                    !data.getPositionProcessor().isInLiquid() &&
                    !data.getPositionProcessor().isOnSlime() &&
                    !data.getPositionProcessor().isOnClimbable() &&
                    !data.getPositionProcessor().isBlockNearHead() &&
                    !data.getPositionProcessor().isInWeb();

            if (invalid) {
                if (++buffer > 2) {
                    fail(String.format("aDy=%.2f, vx=%.2f, vy=%.2f, vz=%.2f", absDeltaY, vel.getX(), vel.getY(), vel.getZ()));
                }
            } else {
                buffer = Math.max(buffer - 0.25, 0);
            }
        }
    }
}
