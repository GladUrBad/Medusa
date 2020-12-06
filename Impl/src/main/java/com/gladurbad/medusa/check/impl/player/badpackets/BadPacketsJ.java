package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "BadPackets (J)", experimental = true, description = "Checks for attacking and digging.")
public class BadPacketsJ extends Check {

    public BadPacketsJ(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final boolean sword = data.getPlayer().getItemInHand().getType().toString().contains("SWORD");
                final boolean invalid = data.getActionProcessor().isSendingDig();

                if (invalid && sword) {
                    if (increaseBuffer() > 5) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(1);
                }
            }
        }
    }
}
