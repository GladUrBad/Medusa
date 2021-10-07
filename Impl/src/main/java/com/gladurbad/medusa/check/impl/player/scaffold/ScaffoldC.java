package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;

@CheckInfo(name = "Scaffold (C)", description = "Checks for downwards scaffold.", experimental = true)
public final class ScaffoldC extends Check {

    public ScaffoldC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            if (!(wrapper.getBlockPosition().getX() == 1 && wrapper.getBlockPosition().getY() == 1 && wrapper.getBlockPosition().getZ() == 1)) {
                if (data.getPlayer().getItemInHand().getType().isBlock()) {
                    if (wrapper.getDirection() == Direction.DOWN) {
                        if (wrapper.getBlockPosition().getY() < data.getPositionProcessor().getY()) fail();
                    }
                }
            }
        }
    }
}
