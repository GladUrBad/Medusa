package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/23/2020 Package com.gladurbad.medusa.check.impl.combat.velocity by GladUrBad
 */
@CheckInfo(name = "Velocity (A)", experimental = true, description = "Checks for vertical velocity.")
public class VelocityA extends Check {

    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (data.getVelocityProcessor().getTicksSinceVelocity() < 5) {
                final double deltaY = data.getPositionProcessor().getDeltaY();

                final double expectedDeltaY = data.getVelocityProcessor().getVelocityY();
                final int percentage = (int) Math.round((deltaY * 100.0) / expectedDeltaY);

                final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                        ExemptType.UNDERBLOCK, ExemptType.TELEPORT, ExemptType.FLYING, ExemptType.WEB);

                final boolean invalid = !exempt && percentage != 100 && expectedDeltaY > 0;

                if (invalid) {
                    if (++buffer > 5) {
                        buffer = 0;
                        fail();
                    }
                } else {
                    buffer = 0;
                }
            }
        }
    }
}
