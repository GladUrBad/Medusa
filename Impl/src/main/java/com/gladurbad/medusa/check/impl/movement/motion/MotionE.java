package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Motion", type = "E")
public class MotionE extends Check {

    private static final double STEP_HEIGHT = 0.6F;
    private int teleportedTicks;

    public MotionE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (++teleportedTicks > 5) {
                final boolean colliding = CollisionUtil.isOnGround(data.getLastLocation(), -0.5001)
                        && CollisionUtil.isOnGround(data.getLocation(), -0.5001);

                if (colliding) {
                    if (data.getDeltaY() > STEP_HEIGHT) {
                        fail();
                    } else {
                        setLastLegitLocation(data.getLocation());
                    }
                }
            }
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleportedTicks = 0;
        }
    }
}