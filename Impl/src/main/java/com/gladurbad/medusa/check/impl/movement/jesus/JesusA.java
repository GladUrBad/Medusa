package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Jesus (A)", description = "Checks for invalid movement in liquids.")
public class JesusA extends Check {

    public JesusA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            /*final boolean toCheck = data.getPositionProcessor().isInLiquid() &&
                    !data.getPositionProcessor().isOnSolidGround();

            if (toCheck) {
                final double deltaY = data.getPositionProcessor().getDeltaY();
                final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

                if ((deltaY == lastDeltaY) || (deltaY == -lastDeltaY)) {
                    if (increaseBuffer() > 4) {
                        fail("dy=" + deltaY + " ldy=" + lastDeltaY);
                    }
                } else {
                    decreaseBufferBy(1);
                }
            }*/
        }
    }
}
