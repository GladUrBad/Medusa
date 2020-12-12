package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

/**
 * Created on 07/01/2020 Package luna.anticheat.checks.player.badpackets by GladUrBad
 */
@CheckInfo(name = "KillAura (E)", experimental = true, description = "Checks for no swing modules.")
public class KillAuraE extends Check {

    private int hits;

    public KillAuraE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if (++hits > 2) {
                    fail();
                }
            }
        } else if (packet.isArmAnimation()) {
            hits = 0;
        }
    }
}
