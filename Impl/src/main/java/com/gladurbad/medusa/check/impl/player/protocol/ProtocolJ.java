package com.gladurbad.medusa.check.impl.player.protocol;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Protocol (J)", description = "Checks for attacking and digging.")
public final class ProtocolJ extends Check {

    public ProtocolJ(final PlayerData data) {
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
                    if (++buffer > 5) {
                        fail();
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }
            }
        }
    }
}
