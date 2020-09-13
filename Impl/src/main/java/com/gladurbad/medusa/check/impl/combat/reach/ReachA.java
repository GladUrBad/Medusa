package com.gladurbad.medusa.check.impl.combat.reach;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.customtype.EvictingList;

import com.gladurbad.medusa.util.customtype.Pair;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;


@CheckInfo(name = "Reach", type = "A")
public class ReachA extends Check {

    private static final ConfigValue reachMaxLatency = new ConfigValue(ConfigValue.ValueType.LONG, "reach-maxlatency");
    private static final ConfigValue reachSensitivity = new ConfigValue(ConfigValue.ValueType.INTEGER, "reach-sensitivity");
    private static final ConfigValue maxReach = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-reach");
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

            if (historyLocations.size() == 20) {
                final long ping = PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer());

                if (ping < reachMaxLatency.getLong()) {
                    final double distance = this.historyLocations.stream()
                      .filter(pair -> Math.abs(now() - pair.getX()) < Math.max(ping, 150L))
                      .mapToDouble(pair -> {
                          Location victimLoc = pair.getY();
                          Location playerLoc = data.getLocation();

                          return playerLoc.toVector().setY(0).distance(victimLoc.toVector().setY(0)) - 0.57D;
                      }).min().orElse(0.0);

                    if (distance > maxReach.getDouble()) {
                        increaseBuffer();
                        if (buffer > REACH_BUFFER) fail();
                    } else {
                        decreaseBuffer();
                    }
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