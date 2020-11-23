package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.steervehicle.WrappedPacketInSteerVehicle;

@CheckInfo(name = "BadPackets (D)", description = "Checks for a common exploit in disabler modules.")
public class BadPacketsD extends Check {

    public BadPacketsD(final PlayerData data) {
        super(data);
    }


    @Override
    public void handle(final Packet packet) {
        if (packet.isSteerVehicle()) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getRawPacket());

            final float forwardValue = Math.abs(wrapper.getForwardValue());
            final float sideValue = Math.abs(wrapper.getSideValue());

            final boolean invalid = forwardValue > .98F || sideValue > .98F;

            if (invalid) {
                fail();
            }
        }
    }
}
