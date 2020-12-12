package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
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
            PositionProcessor movement = data.getPositionProcessor();
            final boolean check = movement.isInLiquid() && !movement.isOnSolidGround();

            if (check) {
                final boolean onWater = movement.isCollidingAtLocation(-0.1, Material.WATER, Material.STATIONARY_WATER) &&
                        movement.isCollidingAtLocation(0.1, Material.AIR)
                        && !movement.isCollidingAtLocation(-0.001, Material.WATER_LILY, Material.CARPET);

                if (onWater) {
                    if (increaseBuffer() > 10) fail();
                } else {
                    decreaseBufferBy(0.15);
                }
            }
        }
    }
}
