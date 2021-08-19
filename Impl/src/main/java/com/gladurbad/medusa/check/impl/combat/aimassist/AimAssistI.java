package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "AimAssist (I)", description = "Checks for snappy rotations.", experimental = true)
public class AimAssistI extends Check {

    private float lastLastDeltaYaw;

    public AimAssistI(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            //teleports can trigger this. best to just ignore on teleports
            if (isExempt(ExemptType.TELEPORT)) return;

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float lastDeltaYaw = data.getRotationProcessor().getLastDeltaYaw();

            debug("dy=" + deltaYaw + " ldy=" + lastDeltaYaw + " lldy=" + lastLastDeltaYaw);

            if (deltaYaw < 5F && lastDeltaYaw > 20F && lastLastDeltaYaw < 5F) {
                if (++buffer > 1.25) {
                    final double low = (deltaYaw + lastLastDeltaYaw) / 2;

                    fail("low=" + low + " high=" + lastDeltaYaw);
                }
            } else buffer = Math.max(buffer - 0.075, 0);

            lastLastDeltaYaw = lastDeltaYaw;
        }
    }
}
