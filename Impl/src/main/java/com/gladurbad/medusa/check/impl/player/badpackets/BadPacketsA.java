package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "BadPackets", type = "A")
public class BadPacketsA extends Check {

    public BadPacketsA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            WrappedPacketInFlying inFlying = new WrappedPacketInFlying(packet.getRawPacket());
            if (Math.abs(inFlying.getPitch()) > 90.0F) {
                fail();
            }
        }
    }
}
