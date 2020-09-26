package com.gladurbad.medusa.check.impl.combat.reach;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.customtype.EvictingList;

import com.gladurbad.medusa.util.customtype.Pair;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;


@CheckInfo(name = "Reach", type = "A")
public class ReachA extends Check {

    private static final ConfigValue reachMaxLatency = new ConfigValue(ConfigValue.ValueType.LONG, "reach-maxlatency");
    private static final ConfigValue reachSensitivity = new ConfigValue(ConfigValue.ValueType.INTEGER, "reach-sensitivity");
    private static final ConfigValue maxReach = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-reach");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");
    private static int REACH_BUFFER = -1;

    private Entity attacked, lastAttacked;
    private final EvictingList<Pair<Long, Location>> historyLocations = new EvictingList<>(20 );

    public ReachA(PlayerData data) {
        super(data);
        if (REACH_BUFFER == -1) {
            REACH_BUFFER = (5 - reachSensitivity.getInt()) * 2;
        }
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
            attacked = useEntity.getEntity();

            if (data.getPlayer().getGameMode() == GameMode.CREATIVE) return;

            if (historyLocations.size() == 20 && PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer()) < reachMaxLatency.getLong()) {
                final double distance = historyLocations.stream()
                        .filter(pair -> Math.abs(now() - pair.getX() - PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer())) < 200)
                        .mapToDouble(pair -> {
                            final Vector playerLoc = data.getPlayer().getLocation().toVector().setY(0);
                            final Vector victimLoc = pair.getY().toVector().setY(0);

                            return playerLoc.distance(victimLoc) - 0.57D;
                        }).min().orElse(0.0);

                if (distance > maxReach.getDouble()) {
                    if (increaseBuffer() > REACH_BUFFER) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(bufferDecay.getDouble());
                }
            }
            lastAttacked = attacked;
        } else if (packet.isFlying()) {
            if (attacked != null && lastAttacked != null) {
                historyLocations.add(new Pair<>(now(), attacked.getLocation()));
            }
        }
    }

}