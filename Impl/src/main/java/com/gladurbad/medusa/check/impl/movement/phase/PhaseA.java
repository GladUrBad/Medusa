package com.gladurbad.medusa.check.impl.movement.phase;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.ReflectionUtil;
import com.gladurbad.medusa.util.type.BoundingBox;
import org.bukkit.block.Block;

import java.util.List;
import java.util.stream.Collectors;

@CheckInfo(name = "Phase (A)", description = "Checks if the player moves through unpassable blocks", experimental = true)
public class PhaseA extends Check {

    public PhaseA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final PositionProcessor position = data.getPositionProcessor();

            final BoundingBox toBB = new BoundingBox(
                    position.getX() - 0.3, position.getX() + 0.3,
                    position.getY(), position.getY() + 1.705,
                    position.getZ() - 0.3, position.getZ() + 0.3
            );

            final BoundingBox fromBB = new BoundingBox(
                    position.getLastX() - 0.3, position.getLastX() + 0.3,
                    position.getLastY(), position.getLastY() + 0.3,
                    position.getLastZ() - 0.3, position.getLastZ() + 0.3
            );

            final BoundingBox unionBox = toBB.union(fromBB);

            if (unionBox.getSize() > 10) return;

            final List<Block> collidedBlocks = unionBox.getBlocks(data.getPlayer().getWorld());

            final int size = (int) collidedBlocks.stream().filter(block -> block.getType().isSolid()).count();

            ReflectionUtil.

            if (size >= 4) {
                fail("collided=" + size);
            }

            debug("collided=" + size);
        }
    }
}
