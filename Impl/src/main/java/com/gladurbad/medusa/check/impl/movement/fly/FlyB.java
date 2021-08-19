package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created by Spiriten.
 */

@CheckInfo(name = "Fly (B)", description = "Checks for air jump.")
public final class FlyB extends Check {

    public FlyB(final PlayerData data) {
        super(data);
    }

    private double lastLastDeltaY;
    private boolean wentDown;

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {

            //handle our exemptions
            boolean isExempt = isExempt(ExemptType.PLACING, ExemptType.CLIMBABLE, ExemptType.STEPPED,
                    ExemptType.LIQUID, ExemptType.SLIME, ExemptType.WEB, ExemptType.TELEPORT,
                    ExemptType.VELOCITY, ExemptType.PISTON);

            //check if their deltaY is less than previous, and set our wentDown boolean to true if so.
            if (data.getPositionProcessor().isInAir() && !data.getPositionProcessor().isOnGround()
                    && !data.getPlayer().isFlying() && !isExempt) {
                if (data.getPositionProcessor().getDeltaY() < data.getPositionProcessor().getLastDeltaY()) wentDown = true;
            } else wentDown = false;

            //check if theyre heading back up after previous going down. dont run if theyre placing blocks (can false)
            if (data.getPositionProcessor().getDeltaY() > data.getPositionProcessor().getLastDeltaY() &&
                    wentDown && data.getPositionProcessor().getDeltaY() > lastLastDeltaY) {
                if (++buffer > 1.25) {
                    fail();
                }
            } else {
                buffer = Math.max(buffer - 0.1, 0);
            }

            lastLastDeltaY = data.getPositionProcessor().getLastDeltaY();
        }
    }
}