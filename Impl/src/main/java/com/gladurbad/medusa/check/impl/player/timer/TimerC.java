package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Timer (C)", description = "Balance based timer check.", experimental = true)
public class TimerC extends Check {

    private static final ConfigValue maxBal = new ConfigValue(ConfigValue.ValueType.LONG, "maximum-balance");
    private static final ConfigValue balReset = new ConfigValue(ConfigValue.ValueType.LONG, "balance-reset");
    private static final ConfigValue balSubOnTp = new ConfigValue(
            ConfigValue.ValueType.LONG, "balance-subtraction-on-teleport"
    );
    private long lastFlyingTime = 0L;
    private long balance = 0L;

    public TimerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.JOINED, ExemptType.TPS)) {
            if (lastFlyingTime != 0L) {
                final long now = now();
                balance += 50L;
                balance -= now - lastFlyingTime;
                if (balance > maxBal.getLong()) {
                    fail("balance=" + balance);
                    balance = balReset.getLong();
                }
            }
            lastFlyingTime = now();
        } else if (packet.isTeleport()) {
            balance -= balSubOnTp.getLong();
        }
    }
}
