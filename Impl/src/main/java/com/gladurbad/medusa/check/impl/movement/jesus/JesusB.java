package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "Jesus (B)", description = "Checks for irregular swimming accelerations.")
public class JesusB extends Check {
    public JesusB(PlayerData data) {
        super(data);
    }

    private double lastAccel;

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final List<Block> blocks = data.getPositionProcessor().getBlocks();

            final boolean touchingLily = blocks.stream().anyMatch(block -> block.getType() == Material.WATER_LILY);
            final boolean touchingWater = data.getPositionProcessor().isInLiquid();
            final boolean nearGround = data.getPositionProcessor().isOnSolidGround();

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.FLYING, ExemptType.DEPTH_STRIDER)
                    || touchingLily
                    || nearGround;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double accel = Math.abs(deltaY - lastDeltaY);
            final double lastAccel = this.lastAccel;

            this.lastAccel = accel;

            final double diff = Math.abs(accel - lastAccel);

            final ClientVersion clientVersion = PlayerUtil.getClientVersion(data.getPlayer());

            final boolean invalidAccel = diff == 0
                    && clientVersion.isLowerThanOrEquals(ClientVersion.v_1_12_2)
                    && Math.abs(deltaY) <= 0.05;

            final double deltaMax = clientVersion.isHigherThanOrEquals(ClientVersion.v_1_13) ? 0.45 : 0.101;

            final boolean invalidDelta = deltaY == 0 || MathUtil.isScientificNotation(deltaY) || deltaY > deltaMax;

            if (!exempt && touchingWater && (invalidAccel || invalidDelta)) {
                if (++buffer > 15) fail("dY= " + deltaY + " diff= " + diff);
            } else {
                buffer = Math.max(buffer - 0.5, 0);
            }
        }
    }
}