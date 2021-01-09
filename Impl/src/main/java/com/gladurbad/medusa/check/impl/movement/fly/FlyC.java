package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 *
 * This is a decent nofall check however it's easily worked around by using the Flying packet and not the Position
 * packet to set on ground values. I would use Flying packet for this but it has a lot of false flag so Position
 * is the way to go here to be more stable.
 */

@CheckInfo(name = "Fly (C)", experimental = true, description = "Checks for ground-spoof.")
public class FlyC extends Check {

    public FlyC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            final double ON_GROUND = 1 / 64D;
            final boolean positionGround = wrapper.getY() % ON_GROUND == 0;
            final boolean packetGround = wrapper.isOnGround();

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.CLIMBABLE,
                    ExemptType.FLYING, ExemptType.JOINED);

            if (!exempt && positionGround != packetGround) {
                if (++buffer > 4) {
                    fail();
                }
            } else {
               buffer = Math.max(buffer - 0.15, 0);
            }
        }
    }
}
