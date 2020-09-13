package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;

@CheckInfo(name = "Motion", type = "D", dev = false)
public class MotionD extends Check {

    public MotionD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && this.isFlyingPacket(packet)) {
            final double deltaY = data.getDeltaY();
            final double lastDeltaY = data.getLastDeltaY();

            if (MathUtil.isScientificNotation(deltaY) && MathUtil.isScientificNotation(lastDeltaY)) {
                fail();
            } else {
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
