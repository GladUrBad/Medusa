package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Killaura", type = "E")
public class KillauraE extends Check {

    private int hits;
    public KillauraE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (useEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if (++hits > 2) {
                    fail();
                }
            }
        } else if (packet.isSwing()) {
            hits = 0;
        }
    }
}
