package com.gladurbad.antimovehack.check.impl.motion;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.network.Packet;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;

@CheckInfo(name = "Motion", type = "C", dev = true)
public class MotionC extends Check {

    public MotionC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            final double deltaY = data.getDeltaY();

            final boolean invalid = !CollisionUtil.isOnGround(data.getLastLocation(), -0.50001) &&
                    !CollisionUtil.isOnGround(data.getLocation(), -0.50001) &&
                    deltaY > 0.0D &&
                    data.getLastDeltaY() <= 0.0D;

            if(invalid) {
                failAndSetback();
            } else {
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
