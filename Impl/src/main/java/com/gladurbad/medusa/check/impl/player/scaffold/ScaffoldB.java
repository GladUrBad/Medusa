package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Scaffold (B)", description = "Checks for packet order.")
public class ScaffoldB extends Check {

    private long lastBlockPlace;
    private boolean placedBlock;

    public ScaffoldB(final PlayerData data) {
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
