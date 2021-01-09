package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Material;

@CheckInfo(name = "Jesus (B)", description = "Checks for water friction.", experimental = true)
public class JesusB extends Check {

    private int ticks;

    public JesusB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final PositionProcessor move = positionInfo();
            final boolean onWater =
                    move.isCollidingAtLocation(-0.1, material -> material.toString().contains("WATER"), PositionProcessor.CollisionType.ANY) &&
                    !move.isCollidingAtLocation(-0.001, material -> material == Material.CARPET || material == Material.WATER_LILY, PositionProcessor.CollisionType.ANY);

            final boolean exempt = isExempt(ExemptType.VELOCITY);

            if (onWater && !exempt) {
                if (++ticks > 10) {
                    final double prediction = move.getLastDeltaXZ() * 0.8F;
                    final double difference = Math.abs(move.getLastDeltaXZ() - prediction);

                    if (difference > 0.03) {
                        if (++buffer > 5) {
                            fail("diff=" + difference);
                        }
                    } else {
                        buffer = Math.max(buffer - 0.5, 0);
                    }
                }
            } else {
                ticks = 0;
            }


        }
    }
}
