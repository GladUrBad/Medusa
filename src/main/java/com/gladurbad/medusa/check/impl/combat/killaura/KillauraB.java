package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import com.google.common.collect.Lists;
import io.github.retrooper.packetevents.enums.minecraft.EntityUseAction;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

import org.bukkit.entity.EntityType;

import java.util.Deque;

@CheckInfo(name = "Killaura", type = "B", dev = true)
public class KillauraB extends Check {

    private int hitTicks;
    private Deque<Double> accels = Lists.newLinkedList();

    public KillauraB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving()) {
            if(packet.getPacketId() == PacketType.Client.POSITION_LOOK) {
                if(++this.hitTicks < 2 && data.isSprinting()) {
                    final double accel = Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ());

                    if(accel < 0.003) increaseBuffer();
                    else if(buffer > 0) decreaseBufferBy(0.5);

                    if(buffer > 4) {
                        fail();
                        decreaseBufferBy(0.5);
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
