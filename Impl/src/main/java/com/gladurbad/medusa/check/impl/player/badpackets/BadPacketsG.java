package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/10/2020 Package com.gladurbad.medusa.check.impl.player.scaffold by GladUrBad
 */

@CheckInfo(name = "BadPackets (G)", description = "Checks for packet order.")
public class BadPacketsG extends Check {

    private long lastBlockPlace;
    private boolean placedBlock;

    public BadPacketsG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            lastBlockPlace = now();
            placedBlock = true;
        } else if (packet.isFlying()) {
            if (placedBlock) {
                final long delay = now() - lastBlockPlace;
                final boolean invalid = !data.getActionProcessor().isLagging() && delay > 15;

                if (invalid) {
                    if (increaseBuffer() > 2) {
                        fail("delay=" + delay + " buffer=" + getBuffer());
                    }
                } else {
                    decreaseBufferBy(0.1);
                }
            }
            placedBlock = false;
        }
    }
}
