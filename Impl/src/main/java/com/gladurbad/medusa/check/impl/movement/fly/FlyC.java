package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 */

@CheckInfo(name = "Fly (C)", experimental = true, description = "Checks for ground-spoof.")
public class FlyC extends Check {

    public FlyC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());
            final boolean serverGround = data.getPositionProcessor().isMathematicallyOnGround();
            final boolean packetGround =  wrapper.isOnGround();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double posY = data.getPositionProcessor().getY();

            final boolean exempt = isExempt(ExemptType.VEHICLE, ExemptType.TELEPORT,
                    ExemptType.BOAT, ExemptType.SLIME, ExemptType.CLIMBABLE, ExemptType.TRAPDOOR, ExemptType.VOID,
                    ExemptType.VELOCITY, ExemptType.FLYING) || data.getPositionProcessor().isInWeb();

            if (!exempt && packetGround != serverGround) {
                if (increaseBuffer() > 3) {
                    fail("clientGround=" + packetGround + " serverGround=" + serverGround + " posY=" + posY + " deltaY=" + deltaY);
                }
            } else {
                decreaseBufferBy(0.15);
            }
        }
    }
}
