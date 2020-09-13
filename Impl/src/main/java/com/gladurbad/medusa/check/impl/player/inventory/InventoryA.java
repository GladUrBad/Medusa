package com.gladurbad.medusa.check.impl.player.inventory;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Inventory", type = "A")

public class InventoryA extends Check {

    private int clickTicks, lastClickTicks;

    public InventoryA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && packet.getPacketId() == PacketType.Client.WINDOW_CLICK) {
            //debug(clickTicks);

            if (clickTicks == 1 || (lastClickTicks == 1 && clickTicks == 0)) {
                increaseBuffer();
                if (buffer > 5) {
                    fail();
                    buffer /= 2;
                }
            } else {
                decreaseBufferBy(2);
            }

            lastClickTicks = clickTicks;
            clickTicks = 0;
        } else if (packet.isReceiving() && isFlyingPacket(packet)) {
            ++clickTicks;
        }
    }
}
