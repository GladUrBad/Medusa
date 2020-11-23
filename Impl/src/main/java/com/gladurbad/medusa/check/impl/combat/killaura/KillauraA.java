package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

@CheckInfo(name = "Killaura", type = "A")
public class KillauraA extends Check {

    private long flyingTime;
    private int ticksSincePacketDrop;

    public KillauraA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            flyingTime = now();
        } else if (packet.isUseEntity()) {
            final long delay = now() - flyingTime;

            if (delay > 85) {
                ticksSincePacketDrop = 0;
            } else {
                ++ticksSincePacketDrop;
            }

            if (delay <= 1 && ticksSincePacketDrop > 35) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.1);
            }
        }
    }
}
