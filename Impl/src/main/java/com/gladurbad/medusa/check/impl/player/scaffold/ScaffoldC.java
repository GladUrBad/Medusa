package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.enums.Direction;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;

@CheckInfo(name = "Scaffold (C)", description = "Checks for downwards scaffold.", experimental = true)
public class ScaffoldC extends Check {

    public ScaffoldC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            if (!(wrapper.getX() == 1 && wrapper.getY() == 1 && wrapper.getZ() == 1)) {
                if (data.getPlayer().getItemInHand().getType().isBlock()) {
                    if (wrapper.getDirection() == Direction.DOWN) {
                        if (wrapper.getY() < data.getPositionProcessor().getY()) fail();
                    }
                }
            }
        }
    }
}
