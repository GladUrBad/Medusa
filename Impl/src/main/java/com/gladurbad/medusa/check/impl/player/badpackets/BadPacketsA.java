package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "BadPackets (A)", description = "Checks for invalid pitch rotation.")
public class BadPacketsA extends Check {

    public BadPacketsA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float pitch = data.getRotationProcessor().getPitch();

            if (Math.abs(pitch) > 90) fail("pitch=" + pitch);
        }
    }
}
