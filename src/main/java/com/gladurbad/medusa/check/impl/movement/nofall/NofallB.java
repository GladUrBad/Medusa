package com.gladurbad.medusa.check.impl.movement.nofall;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Nofall", type = "B", dev = true)
public class NofallB extends Check {

    public NofallB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

            if(!wrappedPacketInFlying.isOnGround()) {
                final double accel = data.getDeltaY() - data.getLastDeltaY();

                if(accel == 0.0D) {
                    increaseBuffer();
                    if(buffer > 10) fail();
                } else {
                    decreaseBuffer();
                    setLastLegitLocation(data.getPlayer().getLocation());
                }
            }
        }
    }
}
