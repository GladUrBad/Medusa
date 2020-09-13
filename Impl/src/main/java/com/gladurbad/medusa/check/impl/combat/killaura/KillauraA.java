package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Killaura", type = "A")
public class KillauraA extends Check {

    private long flyingTime;

    public KillauraA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            flyingTime = now();
        } else if (packet.isUseEntity()) {
            final long delay = now() - flyingTime;
            if (delay <= 0) fail();
        }
    }
}
