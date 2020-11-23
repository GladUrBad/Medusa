package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 *
 * This check checks if the player decelerates when hitting a Player. If they don't decelerate, then
 * they are probably using KeepSprint, commonly found in Killaura.
 */

@CheckInfo(name = "KillAura (B)", experimental = true, description = "Checks for KeepSprint modules.")
public class KillAuraB extends Check {

    public KillAuraB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosLook()) {
            if (data.getExemptProcessor().isExempt(ExemptType.COMBAT)) {
                final boolean sprinting = data.getActionProcessor().isSprinting();
                final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
                final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

                final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);
                final long clickDelay = data.getClickProcessor().getDelay();
                final boolean onGround = data.getPositionProcessor().isMathematicallyOnGround();
                final Entity target = data.getCombatProcessor().getTarget();

                final boolean invalid = acceleration < 0.0025 &&
                        deltaXZ > 0.22 &&
                        onGround &&
                        sprinting &&
                        clickDelay < 250 &&
                        target.getType() == EntityType.PLAYER;

                if (invalid) {
                    if (increaseBuffer() > 5) {
                        fail("acceleration=" + acceleration);
                        decreaseBuffer();
                    }
                } else {
                    decreaseBufferBy(0.5);
                }
            }
        }
    }
}
