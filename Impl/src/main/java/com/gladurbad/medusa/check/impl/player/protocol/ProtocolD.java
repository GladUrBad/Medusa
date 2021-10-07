package com.gladurbad.medusa.check.impl.player.protocol;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.Protocol by GladUrBad
 */


@CheckInfo(name = "Protocol (D)", description = "Checks for a common exploit in disabler modules.")
public final class ProtocolD extends Check {

    public ProtocolD(final PlayerData data) {
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
