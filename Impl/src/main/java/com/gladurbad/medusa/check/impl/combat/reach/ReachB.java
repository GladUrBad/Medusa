package com.gladurbad.medusa.check.impl.combat.reach;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.HitboxExpansion;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach (B)", experimental = true, description = "A basic verbose reach check.")
public final class ReachB extends Check {

    private static final ConfigValue maxLatency = new ConfigValue(ConfigValue.ValueType.LONG, "max-latency");
    private static final ConfigValue maxReach = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-reach");

    public ReachB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (data.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
            if (PlayerUtil.getPing(data.getPlayer()) > (maxLatency.getLong() < 0 ? Integer.MAX_VALUE : maxLatency.getLong())) return;

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final Vector attacker = data.getPlayer().getLocation().toVector().setY(0);
                final Vector victim = data.getCombatProcessor().getTarget().getLocation().toVector().setY(0);

                final double expansion = HitboxExpansion.getExpansion(data.getCombatProcessor().getTarget());
                final double distance = attacker.distance(victim) - expansion;

                debug("dist=" + distance + " buf=" + buffer);
                if (distance > maxReach.getDouble()) {
                    if (++buffer > 20) {
                        buffer = 20;
                        fail(String.format("reach=%.2f, buffer=%.2f", distance, buffer));
                    }
                } else {
                    buffer = Math.max(buffer - 4, 0);
                }
            }
        }
    }
}
