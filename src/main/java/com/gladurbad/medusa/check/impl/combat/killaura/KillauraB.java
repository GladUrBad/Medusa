package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.customtype.EvictingList;

import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

import org.bukkit.entity.EntityType;

@CheckInfo(name = "Killaura", type = "B", dev = true)
public class KillauraB extends Check {

    private int hitTicks;
    private final EvictingList<Long> clickDelays = new EvictingList<>(20);
    private long lastClickTime;
    private double cps;

    public KillauraB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving()) {
            if(packet.getPacketId() == PacketType.Client.POSITION_LOOK) {
                if(++this.hitTicks < 2 && data.isSprinting()) {
                    final double accel = Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ());

                    if(cps < 15) {
                        if(accel < 0.015) {
                            increaseBuffer();
                            if (buffer > 5) {
                                fail();
                            }
                        } else {
                            decreaseBuffer();
                        }
                    }
                }
            } else if(packet.getPacketId() == PacketType.Client.USE_ENTITY) {
                WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
                if(wrappedPacketInUseEntity.getAction()
                        == WrappedPacketInUseEntity.EntityUseAction.ATTACK
                        && wrappedPacketInUseEntity.getEntity().getType() == EntityType.PLAYER) {
                    this.hitTicks = 0;
                }
            } else if(packet.getPacketId() == PacketType.Client.ARM_ANIMATION) {
                final long clickTime = now();
                final long clickDelay = clickTime - lastClickTime;

                clickDelays.add(clickDelay);

                if (clickDelays.size() >= 20) {
                    final double average
                            = clickDelays.parallelStream().mapToDouble(value -> value).average().orElse(0.0);

                    cps = 1000L / average;
                }

                lastClickTime = clickTime;
            }
        }
    }
}