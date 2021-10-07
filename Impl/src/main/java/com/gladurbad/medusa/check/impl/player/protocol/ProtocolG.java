package com.gladurbad.medusa.check.impl.player.protocol;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/10/2020 Package com.gladurbad.medusa.check.impl.player.scaffold by GladUrBad
 */

@CheckInfo(name = "Protocol (G)", description = "Checks for packet order.")
public final class ProtocolG extends Check {

    private long lastBlockPlace;
    private boolean placedBlock;

    public ProtocolG(final PlayerData data) {
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
                final boolean invalid = !data.getActionProcessor().isLagging() && delay > 25;

                if (invalid) {
                    if (++buffer > 5) {
                        fail("delay=" + delay + " buffer=" + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - 2, 0);
                }
            }
            placedBlock = false;
        }
    }
}
