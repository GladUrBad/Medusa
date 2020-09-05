package com.gladurbad.medusa.check.impl.movement.nofall;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;


@CheckInfo(name = "Nofall", type = "A", dev = true)
public class NofallA extends Check {

    private int ticksSinceInVehicle;
    public NofallA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && this.isFlyingPacket(packet)) {
            final WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

            if (wrappedPacketInFlying.isPosition()) {
                final boolean serverOnGround = wrappedPacketInFlying.isOnGround();
                final boolean clientOnGround = data.getLocation().getY() % (1D / 64) == 0.0;

                final boolean invalid = CollisionUtil.isInLiquid(data.getPlayer()) && CollisionUtil.isCollidingWithClimbable(data.getPlayer());

                if(data.getPlayer().isInsideVehicle()) ticksSinceInVehicle = 0;
                else ++ticksSinceInVehicle;

                if(!invalid && serverOnGround != clientOnGround && ticksSinceInVehicle > 10) {
                    increaseBuffer();
                    if(buffer > 5) {
                        fail();
                    }
                } else {
                    buffer = 0;
                    setLastLegitLocation(data.getPlayer().getLocation());
                }
            }
        }
    }
}