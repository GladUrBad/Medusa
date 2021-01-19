package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "Jesus (A)", description = "Checks for water friction.", experimental = true)
public class JesusA extends Check {

    private int ticks;

    public JesusA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final List<Block> blocks = data.getPositionProcessor().getBlocks();

            final boolean touchingLily = blocks.stream().anyMatch(block -> block.getType() == Material.WATER_LILY);
            final boolean touchingWater = data.getPositionProcessor().isInLiquid();

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.FLYING, ExemptType.DEPTH_STRIDER)
                    || touchingLily;

            if (!exempt && touchingWater) {
                if (++ticks > 10) {
                    final double prediction = data.getPositionProcessor().getLastDeltaXZ() * 0.8F;
                    final double difference = Math.abs(data.getPositionProcessor().getLastDeltaXZ() - prediction);

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
