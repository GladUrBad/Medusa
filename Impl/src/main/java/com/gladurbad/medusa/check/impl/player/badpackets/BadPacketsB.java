package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "BadPackets", type = "B", dev = true)
public class BadPacketsB extends Check {

    public BadPacketsB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
    }
}