package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

/**
 * Created on 07/01/2020 Package luna.anticheat.checks.player.badpackets by GladUrBad
 *
 * In Minecraft, each time you attack a player, you must swing too. This check makes sure they do that.
 * That's about it.
 */
@CheckInfo(name = "KillAura (E)", description = "Checks for no swing modules.")
public class KillAuraE extends Check {

    private Packet lastPacket;

    public KillAuraE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (packet.isUseEntity()) {
                if (!lastPacket.isArmAnimation()) {
                    fail("lp=" + lastPacket.getRawPacket().getClass().getSimpleName() +
                            " p=" + packet.getRawPacket().getClass().getSimpleName());
                }
            }
            lastPacket = packet;
        }
    }
}
