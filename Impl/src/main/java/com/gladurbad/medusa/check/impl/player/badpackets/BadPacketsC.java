package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.badpackets by GladUrBad
 */


@CheckInfo(name = "BadPackets (C)", description = "Checks for flying packet sequence.")
public class BadPacketsC extends Check {

    private int streak;

    public BadPacketsC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            if (wrapper.isPosition() || data.getPlayer().isInsideVehicle()) {
                streak = 0;
                return;
            }

            if (++streak > 20) {
                fail("streak=" + streak);
            }
        } else if (packet.isSteerVehicle()) {
            streak = 0;
        }
    }
}
