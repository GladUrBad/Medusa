package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Killaura", type = "G", dev = true)
public class KillauraG extends Check {

    private int hits;
    public KillauraG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (packet.getPacketId() == PacketType.Client.USE_ENTITY) {
                ++hits;
                if (hits > 1) {
                    fail();
                }
            } else if (packet.getPacketId() == PacketType.Client.ARM_ANIMATION) {
                hits = 0;
            }
        }
    }
}
