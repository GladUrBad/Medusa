package com.gladurbad.medusa.check.impl.combat.aim;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Aim", type = "C")
public class AimC extends Check {

    private int teleportTicks;

    public AimC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final boolean goodToCheck = data.getDeltaYaw() != 0 && data.getLastDeltaYaw() != 0 && ++teleportTicks > 0;
            final float yawAcceleration = Math.abs(data.getDeltaYaw() - data.getLastDeltaYaw());

            if (goodToCheck) {
                if (yawAcceleration == 0) {
                    increaseBuffer();
                    if (buffer > 8) {
                        fail();
                    }
                }
            } else {
                decreaseBuffer();
            }
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleportTicks = 0;
        }
    }
}
