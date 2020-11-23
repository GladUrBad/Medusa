package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "Speed", type = "C", dev = true)
public class SpeedC extends Check {

    private int teleportedTicks;
    public SpeedC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition() && ++teleportedTicks > 20) {
            final double acceleration = Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ());

            final boolean invalid = acceleration > MathUtil.getBaseSpeed(data.getPlayer()) &&
                    data.getTicksSinceVelocity() > 20 &&
                    !data.getPlayer().isInsideVehicle() &&
                    !data.getPlayer().isFlying();

            if (invalid) {
                fail();
            } else {
                setLastLegitLocation(data.getBukkitLocation());
            }

        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleportedTicks = 0;
        }
    }
}