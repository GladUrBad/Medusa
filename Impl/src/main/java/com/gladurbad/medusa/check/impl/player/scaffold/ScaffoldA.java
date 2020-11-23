package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Scaffold (A)",  description = "Checks for movement/rotations that are related to scaffold modules.")
public class ScaffoldA extends Check {

    private boolean placedBlock;

    public ScaffoldA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (placedBlock && isBridging()) {
                final double deltaXz = data.getPositionProcessor().getDeltaXZ();
                final double lastDeltaXz = data.getPositionProcessor().getLastDeltaXZ();
                final double accel = Math.abs(deltaXz - lastDeltaXz);

                final float deltaYaw = data.getRotationProcessor().getDeltaYaw() % 360F;
                final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

                final boolean invalid = deltaYaw > 75F && deltaPitch > 15F && accel < 0.15;

                if (invalid) {
                    if (increaseBuffer() > 3) {
                        fail(String.format("accel=%.2f, deltaYaw=%.2f, deltaPitch=%.2f", accel, deltaYaw, deltaPitch));
                    }
                } else {
                    decreaseBufferBy(0.5);
                }
            }
            placedBlock = false;
        } else if (packet.isBlockPlace()) {
            if (data.getPlayer().getItemInHand().getType().isBlock()) {
                placedBlock = true;
            }
        }
    }
}
