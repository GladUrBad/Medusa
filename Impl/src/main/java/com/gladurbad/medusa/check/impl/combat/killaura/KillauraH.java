package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Killaura", type = "H", dev = true)
public class KillauraH extends Check {

    private boolean attacked;
    private int ticks;
    public KillauraH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (useEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                attacked = true;
            }
        } else if (packet.isReceiving() && packet.getPacketId() == PacketType.Client.BLOCK_PLACE) {
            if (attacked) {
                if (ticks == 1) fail();
                ticks = 0;
            }
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
