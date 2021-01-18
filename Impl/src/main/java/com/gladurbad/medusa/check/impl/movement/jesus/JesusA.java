package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
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
        if (packet.isPosition() && data.getPositionProcessor().isInLiquid() && !data.getPositionProcessor().isOnSolidGround()) {
            final boolean floating = data.getPositionProcessor().isCollidingAtLocation(
                    0.1, material -> material == Material.AIR, PositionProcessor.CollisionType.ALL
            ) && PlayerUtil.shouldCheckJesus(data);

            if (floating) {
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
