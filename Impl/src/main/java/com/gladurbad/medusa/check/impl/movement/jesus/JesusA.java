package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Material;

/**
 * Created on 11/20/2020 Package com.gladurbad.medusa.check.impl.movement.jesus by GladUrBad
 */

@CheckInfo(name = "Jesus (A)", description = "Checks for invalid movement in liquids.")
public class JesusA extends Check {

    public JesusA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            PositionProcessor movement = positionInfo();
            final boolean check = movement.isInLiquid() && !movement.isOnSolidGround();

            if (check) {
                player().isOnGround();
                //So fucking ugly, going to fix this later.
                final boolean onWater = movement.isCollidingAtLocation(-0.1, material -> material.toString().contains("WATER"), PositionProcessor.CollisionType.ALL) &&
                        movement.isCollidingAtLocation(0.1, material -> material == Material.AIR, PositionProcessor.CollisionType.ALL)
                        && !movement.isCollidingAtLocation(-0.001, material -> material == Material.CARPET || material == Material.WATER_LILY, PositionProcessor.CollisionType.ANY);

                if (onWater) {
                    if (++buffer > 10) {
                        buffer /= 2;
                        fail();
                    }
                } else {
                    buffer = Math.max(buffer - 2, 0);
                }
            }
        }
    }
}
