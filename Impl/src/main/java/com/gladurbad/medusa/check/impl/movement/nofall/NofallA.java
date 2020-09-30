package com.gladurbad.medusa.check.impl.movement.nofall;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Nofall", type = "A")
public class NofallA extends Check {

    private int ticksSinceInVehicle, teleportTicks;

    public NofallA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final WrappedPacketInFlying flying = new WrappedPacketInFlying(packet.getRawPacket());

            if (flying.isPosition() && ++teleportTicks > 10) {
                final boolean serverOnGround = flying.isOnGround();
                final boolean clientOnGround = data.getLocation().getY() % 0.015625 == 0.0;

                final boolean invalid = CollisionUtil.isInLiquid(data.getPlayer())
                        || CollisionUtil.isCollidingWithClimbable(data.getPlayer())
                        || CollisionUtil.isNearBoat(data.getPlayer())
                        || data.getPlayer().isFlying();


                if (data.getPlayer().isInsideVehicle()) {
                    ticksSinceInVehicle = 0;
                } else {
                    ++ticksSinceInVehicle;
                }

                if (!invalid && serverOnGround != clientOnGround && ticksSinceInVehicle > 10) {
                    increaseBuffer();
                    if (buffer > 5) {
                        fail();
                    }
                } else {
                    buffer = 0;
                    setLastLegitLocation(data.getPlayer().getLocation());
                }
            }
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleportTicks = 0;
        }
    }
}