package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Scaffold", type = "A", dev = true)
public class ScaffoldA extends Check {

    private long flyingTime, placementTime;

    public ScaffoldA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (packet.isFlying()) {
                flyingTime = now();
            } else if (packet.getPacketId() == PacketType.Client.BLOCK_PLACE) {
                if (!data.getPlayer().getItemInHand().getType().isBlock()) return;

                placementTime = now();

                final long timeDifference = placementTime - flyingTime;

                if (timeDifference == 0) {
                    fail();
                }
            }
        }
    }
}
