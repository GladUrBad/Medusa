package com.gladurbad.medusa.check.impl.combat.hitbox;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.HitboxExpansion;
import com.gladurbad.medusa.util.PlayerUtil;
import com.gladurbad.medusa.util.type.BoundingBox;
import com.gladurbad.medusa.util.type.RayTrace;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.combat.reach by GladUrBad
 */

@CheckInfo(name = "HitBox (A)", experimental = true, description = "Checks for the angle of the attack.")
public final class HitBoxA extends Check {

    private static final ConfigValue maxLatency = new ConfigValue(ConfigValue.ValueType.LONG, "max-latency");
    private static final ConfigValue raytraceSensitivityLevel = new ConfigValue(
            ConfigValue.ValueType.INTEGER, "raytrace-sensitivity-level"
    );

    public HitBoxA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();


            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK
                    || data.getPlayer().getGameMode() != GameMode.SURVIVAL
                    || !(target instanceof Player || target instanceof Villager)
                    || target != lastTarget
                    || !data.getTargetLocations().isFull()
                    || PlayerUtil.getPing(data.getPlayer()) > (maxLatency.getLong() < 0 ? Integer.MAX_VALUE : maxLatency.getLong())) return;

            final int ticks = Medusa.INSTANCE.getTickManager().getTicks();
            final int pingTicks = NumberConversions.floor(data.getActionProcessor().getPing() / 50.0) + 3;

            final RayTrace rayTrace = new RayTrace(data.getPlayer());

            final int collided = (int) data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(ticks - pair.getY() - pingTicks) < 3)
                    .filter(pair -> {
                        final Location location = pair.getX();
                        final BoundingBox boundingBox = new BoundingBox(
                                location.getX() - 0.4,
                                location.getX() + 0.4,
                                location.getY(),
                                location.getY() + 1.9,
                                location.getZ() - 0.4,
                                location.getZ() + 0.4
                        );

                        return boundingBox.collidesD(rayTrace, 0, 6) != 10;
                    }).count();

            final int sensitivity = raytraceSensitivityLevel.getInt();

            debug("collided=" + collided + " sens=" + sensitivity + " buf=" + buffer);

            if (collided <= sensitivity) {
                if (++buffer > 10) {
                    fail("collided=" + collided + " buffer=" + buffer);
                }
            } else {
                buffer -= buffer > 0 ? 1 : 0;
            }
        }
    }
}