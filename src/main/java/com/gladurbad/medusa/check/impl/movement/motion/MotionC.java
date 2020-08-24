package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

@CheckInfo(name = "Motion", type = "C", dev = true)
public class MotionC extends Check {

    public MotionC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            final double deltaY = data.getDeltaY();
            final double lastDeltaY = data.getLastDeltaY();

            final boolean invalid = deltaY == -lastDeltaY && deltaY != 0.0D && deltaY != -3.92;

            if(invalid) {
                increaseBuffer();
                if(buffer > 3) {
                    failAndSetback();
                }
            } else {
                decreaseBuffer();
                this.setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
