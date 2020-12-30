package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Killaura (F)", experimental = true, description = "Checks for hit occlusion (wallhit).")
public class KillauraF extends Check {

    private Location lastAttackerLocation;
    private float lastYaw, lastPitch;

    public KillauraF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final Entity target = data.getCombatProcessor().getTarget();
            final Player attacker = data.getPlayer();

            if (target == null || attacker == null ) return;

            final Location targetLocation = target.getLocation();
            final Location attackerLocation = attacker.getLocation();

            final float yaw = data.getRotationProcessor().getYaw() % 360F;
            final float pitch = data.getRotationProcessor().getPitch();

            if (lastAttackerLocation != null) {
                final boolean check = yaw != lastYaw &&
                        pitch != lastPitch &&
                        attackerLocation.distance(lastAttackerLocation) > 0.1;

                if (check && !attacker.hasLineOfSight(target)) {
                    if (increaseBuffer(10) > 50) {
                        fail();
                    }
                } else {
                    decreaseBuffer(20);
                }
            }

            lastAttackerLocation = attacker.getLocation();

            lastYaw = yaw;
            lastPitch = pitch;
        }
    }
}
