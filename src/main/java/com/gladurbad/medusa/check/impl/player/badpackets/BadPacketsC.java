package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.helditemslot.WrappedPacketInHeldItemSlot;

@CheckInfo(name = "BadPackets", type = "C", dev = true)
public class BadPacketsC extends Check {

    private int lastHeldItemSlot;

    public BadPacketsC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (packet.getPacketId() == PacketType.Client.HELD_ITEM_SLOT) {
                final WrappedPacketInHeldItemSlot wrappedPacketInHeldItemSlot = new WrappedPacketInHeldItemSlot(packet.getRawPacket());

                final int heldItemSlot = wrappedPacketInHeldItemSlot.getItemInHandIndex();

                if(heldItemSlot == lastHeldItemSlot) fail();

                lastHeldItemSlot = heldItemSlot;
            }
        }
    }
}
