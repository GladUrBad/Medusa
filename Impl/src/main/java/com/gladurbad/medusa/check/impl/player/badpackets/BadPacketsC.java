package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.helditemslot.WrappedPacketInHeldItemSlot;

@CheckInfo(name = "BadPackets", type = "C")
public class BadPacketsC extends Check {

    private int lastSlot;

    public BadPacketsC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && packet.getPacketId() == PacketType.Client.HELD_ITEM_SLOT) {
            final WrappedPacketInHeldItemSlot heldItemSlot = new WrappedPacketInHeldItemSlot(packet.getRawPacket());
            final int slot = heldItemSlot.getItemInHandIndex();

            if (slot == lastSlot) {
                fail();
            }
            lastSlot = slot;
        }
    }
}