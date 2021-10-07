package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "KillAura (G)", description = "Checks for large head movements without decelerating.", experimental = true)
public class KillAuraG extends Check {

    public KillAuraG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosLook() && isExempt(ExemptType.COMBAT)) {
            final double deltaXz = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXz = data.getPositionProcessor().getLastDeltaXZ();
            final double accelXz = Math.abs(deltaXz - lastDeltaXz);

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            debug("accel=" + accelXz + " dY=" + deltaYaw);

            if (deltaYaw > 35 && accelXz < 0.00001) {
                fail(String.format("accel=%.6f, dY=%.2f", accelXz, deltaYaw));
            }
        }
    }
}
