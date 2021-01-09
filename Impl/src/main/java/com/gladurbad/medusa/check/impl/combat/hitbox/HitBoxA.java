package com.gladurbad.medusa.check.impl.combat.hitbox;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.HitboxExpansion;
import com.gladurbad.medusa.util.MathUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.combat.reach by GladUrBad
 *
 * Fix stupid HitboxExpansion using NMS.
 */

@CheckInfo(name = "HitBox (A)", description = "Checks for attacking distance.")
public class HitBoxA extends Check {

    public HitBoxA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            final Entity target = combatInfo().getTarget();
            final Entity lastTarget = combatInfo().getLastTarget();

            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK
                    || player().getGameMode() != GameMode.SURVIVAL
                    || !(target instanceof LivingEntity)
                    || target != lastTarget
                    || !data.getTargetLocations().isFull()) return;

            final int ticks = Medusa.INSTANCE.getTickManager().getTicks();
            final int pingTicks = NumberConversions.floor(actionInfo().getPing() / 50.0) + 3;

            final Vector player = player().getLocation().toVector().setY(0);

            final double distance = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(ticks - pair.getY() - pingTicks) < 3)
                    .mapToDouble(pair -> {
                        final Vector victim = pair.getX().toVector().setY(0);
                        final double expansion = HitboxExpansion.getExpansion(target);
                        return player.distance(victim) - expansion;
                    }).min().orElse(0);


            if (distance > 3) {
                if (++buffer > 3) {
                    fail(String.format("reach=%.2f, buffer=%.2f", distance, buffer));
                }
            } else {
                buffer = Math.max(buffer - 0.1, 0);
            }
        }
    }

}