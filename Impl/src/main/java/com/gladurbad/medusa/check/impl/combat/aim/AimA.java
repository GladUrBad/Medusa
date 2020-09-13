package com.gladurbad.medusa.check.impl.combat.aim;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;

@CheckInfo(name = "Aim", type = "A")
public class AimA extends Check {

    public AimA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            if (MathUtil.isScientificNotation(data.getDeltaPitch()) && MathUtil.isScientificNotation(data.getLastDeltaPitch()) && data.getDeltaYaw() > .5) {
                increaseBuffer();
                if (buffer > 5) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
