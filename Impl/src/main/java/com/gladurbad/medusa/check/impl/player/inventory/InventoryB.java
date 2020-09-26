package com.gladurbad.medusa.check.impl.player.inventory;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Inventory", type = "B", dev = true)

public class InventoryB extends Check {
    public InventoryB(PlayerData data) { super(data); }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (data.getDeltaXZ() > 0.14) {
                if (data.isInInventory() && (System.currentTimeMillis() - data.getTimeInInventory()) > 1500) {
                    increaseBuffer();
                    if (buffer > 2) {
                        fail();
                        buffer /= 2;
                    }
                } else {
                    decreaseBufferBy(3);
                }
            }
        }
    }
}
