package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Fly (C)", description = "Checks for ground-spoof.")
public class FlyC extends Check {

    public FlyC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final boolean serverGround = data.getPositionProcessor().isMathematicallyOnGround();
            final boolean clientGround = data.getPositionProcessor().isOnGround();

            final boolean exempt = this.isExempt(ExemptType.VEHICLE, ExemptType.TELEPORT,
                    ExemptType.BOAT, ExemptType.SLIME);

            if (!exempt) {
                if (clientGround != serverGround) {
                    if (increaseBuffer() > 1.75) {
                        fail("clientGround=" + clientGround + " serverGround=" + serverGround);
                    }
                } else {
                    decreaseBufferBy(0.15);
                }
            }
        }
    }
}
