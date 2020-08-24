package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.enums.minecraft.EntityUseAction;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.EntityType;

@CheckInfo(name = "Killaura", type = "B", dev = true)
public class KillauraB extends Check {

    private int hitTicks;

    public KillauraB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving()) {
            if(this.isFlyingPacket(packet)) {
                if(++this.hitTicks < 2 && data.isSprinting()) {
                    final double accel = data.getDeltaXZ() - data.getLastDeltaXZ();

                    if(accel < 0.027) {
                        increaseBuffer();
                        if(buffer > 4) fail();
                    } else {
                        buffer = 0;
                    }
                }
            } else if(packet.getPacketId() == PacketType.Client.USE_ENTITY) {
                WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
                if(wrappedPacketInUseEntity.getAction() == EntityUseAction.ATTACK && wrappedPacketInUseEntity.getEntity().getType() == EntityType.PLAYER) {
                    this.hitTicks = 0;
                }
            }
        }
    }
}
