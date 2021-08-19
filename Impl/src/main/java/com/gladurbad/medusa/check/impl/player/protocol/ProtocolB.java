package com.gladurbad.medusa.check.impl.player.protocol;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.abilities.WrappedPacketInAbilities;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.Protocol by GladUrBad
 */


@CheckInfo(name = "Protocol (B)", description = "Checks for spoofed abilities packets.")
public final class ProtocolB extends Check {

    public ProtocolB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isAbilities()) {
            final WrappedPacketInAbilities wrapper = new WrappedPacketInAbilities(packet.getRawPacket());

            final boolean invalid = wrapper.isFlying() && !data.getPlayer().getAllowFlight() ||
                    //found the idea for the orElse on stack overflow. not sure how well it works
                    wrapper.isFlightAllowed().orElse(false) && !data.getPlayer().getAllowFlight();

            if (invalid) {
                fail();
            }
        }
    }
}
