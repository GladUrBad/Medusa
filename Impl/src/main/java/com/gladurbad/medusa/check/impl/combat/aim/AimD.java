package com.gladurbad.medusa.check.impl.combat.aim;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

@CheckInfo(name = "Aim", type = "D")
public class AimD extends Check {

    private int hitTicks;
    private static final ConfigValue combatOnly = new ConfigValue(ConfigValue.ValueType.BOOLEAN, "combat-only");

    public AimD(PlayerData data) {
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

            if (data.getDeltaYaw() != 0 && data.getLastDeltaYaw() != 0) {
                if (data.getDeltaYaw() > 3 && data.getLastDeltaYaw() > 3 && data.getDeltaPitch() == 0) {
                    increaseBuffer();
                    if (buffer > 6) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(2);
                }
            }
        } else  if (packet.isUseEntity()) {
            hitTicks = 0;
        }
    }
}
