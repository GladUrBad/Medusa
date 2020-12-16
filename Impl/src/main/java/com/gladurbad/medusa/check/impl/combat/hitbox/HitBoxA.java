package com.gladurbad.medusa.check.impl.combat.hitbox;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.HitboxExpansion;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.combat.reach by GladUrBad
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
            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK ||
                data.getPlayer().getGameMode() != GameMode.SURVIVAL ||
                data.getCombatProcessor().getTarget() != data.getCombatProcessor().getLastTarget()) return;

            final int ticks = Medusa.INSTANCE.getTickManager().getTicks();
            final int latencyTicks = NumberConversions.floor(PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer()) / 50.) + 3;

            final Vector playerLocAsVector = data.getPlayer().getLocation().toVector().setY(0);
            if (data.getTargetLocations().isFull()) {
                final Entity target = data.getCombatProcessor().getTarget();
                final double expansion = HitboxExpansion.getExpansion(target);

                final double distance = data.getTargetLocations().stream()
                        .filter(pair -> Math.abs(ticks - pair.getY() - latencyTicks) < 3)
                        .mapToDouble(pair -> {
                            Location location = pair.getX();
                            return location.toVector().setY(0).distance(playerLocAsVector) - expansion;
                        }).min().orElse(-1);

                if (distance > 3.1) {
                    if (increaseBuffer() > 4) {
                        fail(String.format("dist=%.2f, buf=%.2f, exp=%.2f, ent=%s", distance, getBuffer(), expansion, target.getType().getName()));
                        if (getBuffer() > 8) decreaseBufferBy(2);
                    }
                } else {
                    decreaseBufferBy(0.03);
                }
            }
        }
    }

}