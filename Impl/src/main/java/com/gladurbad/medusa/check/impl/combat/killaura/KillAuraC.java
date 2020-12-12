package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 */
@CheckInfo(name = "KillAura (C)", description = "Checks for entities hit in one tick.")
public class KillAuraC extends Check {

    public KillAuraC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final int targets = data.getCombatProcessor().getCurrentTargets();
            if (targets > 1) fail("targets=" + targets);
        }
    }
}
