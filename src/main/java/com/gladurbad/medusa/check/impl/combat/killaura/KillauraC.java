package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Killaura", type = "C", dev = false)
public class KillauraC extends Check {

    private int entitiesHit;
    private int lastEntityId;

    public KillauraC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (this.isFlyingPacket(packet)) {
                entitiesHit = 0;
            } else if (packet.getPacketId() == PacketType.Client.USE_ENTITY) {
                final WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
                final int entityId = wrappedPacketInUseEntity.getEntityId();

                if (entityId != lastEntityId) {
                    if (++entitiesHit > 1) {
                        fail();
                    }
                }

                lastEntityId = entityId;
            }
        }
    }
}
