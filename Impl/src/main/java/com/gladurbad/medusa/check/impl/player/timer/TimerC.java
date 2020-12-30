package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Timer (C)", description = "Balance based timer check.", experimental = true)
public class TimerC extends Check {

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
                if (balance > 0L) {
                    fail("balance=" + balance);
                    balance = -50L;
                }
            }
            lastFlyingTime = now();
        } else if (packet.isTeleport()) {
            balance -= 50L;
        }
    }
}
