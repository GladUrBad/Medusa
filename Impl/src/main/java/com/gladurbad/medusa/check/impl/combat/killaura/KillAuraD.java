package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 */

@CheckInfo(name = "KillAura (D)", experimental = true, description = "Checks for high accuracy.")
public class KillAuraD extends Check {


    public KillAuraD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final double ratio = data.getCombatProcessor().getHitMissRatio();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final double deltaXz = data.getPositionProcessor().getDeltaXZ();

            final boolean invalid = ratio > 98 && deltaYaw > 3F && deltaXz > 0.1;

            if (invalid) {
                if (increaseBuffer() > 30) {
                    fail(String.format("ratio=%.2f, buffer=%.2f", ratio, getBuffer()));
                    setBuffer(30);
                }
            } else {
                decreaseBufferBy(5);
            }
        }
    }
}
