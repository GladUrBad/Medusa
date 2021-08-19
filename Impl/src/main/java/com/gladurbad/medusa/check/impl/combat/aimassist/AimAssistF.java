package com.gladurbad.medusa.check.impl.combat.aimassist;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created 11/14/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "AimAssist (F)", description = "Checks for impossible rotation.", experimental = true)
public final class AimAssistF extends Check {

    public AimAssistF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            debug("tp=" + isExempt(ExemptType.TELEPORT, ExemptType.INSIDE_VEHICLE));

            if (deltaPitch == 0 && deltaYaw == 0 && !isExempt(ExemptType.TELEPORT, ExemptType.INSIDE_VEHICLE) &&
            data.getPositionProcessor().getSinceTeleportTicks() > 0) fail(
                    " tst=" + data.getPositionProcessor().getSinceTeleportTicks()
            );
        }
    }
}
