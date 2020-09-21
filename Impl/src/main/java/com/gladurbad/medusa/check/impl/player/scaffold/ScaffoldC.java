package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "Scaffold", type = "C", dev = true)
public class ScaffoldC extends Check {

    private double lastDeltaY;

    public ScaffoldC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (packet.getPacketId() == PacketType.Client.BLOCK_PLACE) {
                if (!data.getPlayer().getItemInHand().getType().isBlock()) return;
                final double deltaY = data.getDeltaY();
                final double acceleration = Math.abs(deltaY - lastDeltaY);

                if (deltaY > 0) {
                    if (acceleration == 0.0) {
                        increaseBuffer();
                        if (buffer > 8) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(2);
                    }
                }

                lastDeltaY = deltaY;
            }
        }
    }
}
