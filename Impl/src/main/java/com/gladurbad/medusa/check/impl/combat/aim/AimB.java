package com.gladurbad.medusa.check.impl.combat.aim;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

@CheckInfo(name = "Aim", type = "B")
public class AimB extends Check {

    private int hitTicks;
    private static final ConfigValue combatOnly = new ConfigValue(ConfigValue.ValueType.BOOLEAN, "combat-only");

    public AimB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {

            if (combatOnly.getBoolean()) {
                if (++hitTicks > 5) {
                    return;
                }
            }

            if (data.getDeltaYaw() % .25 == 0 && data.getDeltaYaw() > 0) {
                increaseBuffer();
                if (buffer > 3) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }
        } else if (packet.isUseEntity()) {
            hitTicks = 0;
        }
    }
}
