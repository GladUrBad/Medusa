package com.gladurbad.medusa.check.impl.movement.nofall;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;


@CheckInfo(name = "Nofall", type = "A", dev = true)
public class NofallA extends Check {

    public NofallA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && this.isFlyingPacket(packet)) {
            final boolean serverOnGround = data.getPlayer().isOnGround();
            final boolean clientOnGround = data.getLocation().getY() % (1D/64) == 0;

            if(serverOnGround && !clientOnGround) {
                increaseBuffer();
                if(buffer > 1) fail();
            } else {
                decreaseBuffer();
            }
        }
    }
}
