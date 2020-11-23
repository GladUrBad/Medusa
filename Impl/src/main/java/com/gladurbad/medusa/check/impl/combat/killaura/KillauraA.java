package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 *
 * This check ensures the client sends packets in the correct order while attacking. Flags older clients usually.
 * This check needs an overhaul. While it should not false under most conditions, occasionally it might false with lag.
 * It also is not very good at flagging under such conditions.
 * Although, I would rather this not false than detect more, since what this
 * checks for doesn't provide an advantage.
 *
 * Packet order: ArmAnimation -> UseEntity -> Instance of flying packet.
 */

@CheckInfo(name = "KillAura (A)", description = "Checks for packet order.")
public class KillAuraA extends Check {

    private boolean usedEntity;
    private long lastUseEntityTime;

    public KillAuraA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            usedEntity = true;
            lastUseEntityTime = now();
        } else if (packet.isFlying()) {
            if (usedEntity) {
                final long delay = now() - lastUseEntityTime;
                final boolean invalid = !data.getActionProcessor().isLagging() && delay > 15;

                if (invalid) {
                    if (increaseBuffer() > 2) {
                        fail("delay=" + delay + " buffer=" + getBuffer());
                    }
                } else {
                    decreaseBufferBy(0.15);
                }
            }
            usedEntity = false;
        }
    }
}
