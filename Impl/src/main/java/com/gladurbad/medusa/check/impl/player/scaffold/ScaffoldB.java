package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Scaffold", type = "B", dev = true)
public class ScaffoldB extends Check {

    private boolean placedBlock;

    public ScaffoldB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (packet.isFlying()) {
                if (placedBlock) {
                    final double accel = data.getDeltaXZ() - data.getLastDeltaXZ();

                    if (data.getDeltaYaw() > 90F) {
                        if (accel > 0) {
                            increaseBuffer();
                            if (buffer > 2) {
                                fail();
                            }
                        } else {
                            decreaseBuffer();
                        }
                    }
                    placedBlock = false;
                }
            } else if (packet.getPacketId() == PacketType.Client.BLOCK_PLACE) {
                if (data.getPlayer().getItemInHand().getType().isBlock()) {
                    placedBlock = true;
                }
            }
        }
    }
}
