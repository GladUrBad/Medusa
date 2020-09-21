package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

@CheckInfo(name = "AutoClicker", type = "A")
public class AutoClickerA extends Check {

    private int ticks, cps;
    private static final ConfigValue maxCPS = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-cps");

    public AutoClickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && !data.isDigging()) {
            if (++ticks >= 20) {
                if (cps > maxCPS.getInt()) {
                    fail();
                }

                ticks = 0;
                cps = 0;
            }
        } else if (packet.isSwing()) {
            ++cps;
        }
    }
}
